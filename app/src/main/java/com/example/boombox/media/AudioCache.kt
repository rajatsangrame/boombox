package com.example.boombox.media

import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.util.Log
import android.util.Pair
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object AudioCache {
  private const val TAG = "Audio"
  private var downloadCache: SimpleCache? = null
  private const val oneGB: Long = (1024 * 1024 * 1024).toLong()

  //region DownloadFileUsingVideoCache
  private var totalBytesToRead = 0L
  private var bytesReadFromCache: Long = 0
  private var bytesReadFromNetwork: Long = 0

  //endregion

  private val maxCacheSize: Long by lazy {
    if (getAvailableInternalStorage() / 10L > oneGB) {
      getAvailableInternalStorage() / 10L
    } else {
      oneGB
    }
  }

  fun getInstance(context: Context): SimpleCache {
    val evictor = LeastRecentlyUsedCacheEvictor(maxCacheSize)
    val databaseProvider: DatabaseProvider = ExoDatabaseProvider(context)
    if (downloadCache == null) downloadCache =
      SimpleCache(File(context.cacheDir, "audio_cache"), evictor, databaseProvider)
    return downloadCache as SimpleCache
  }

  @JvmStatic
  fun getCacheSize(context: Context): Long? {
    if (downloadCache == null) {
      getInstance(context)
    }
    return downloadCache?.cacheSpace
  }

  @JvmStatic
  fun getCacheCount(context: Context): Int? {
    if (downloadCache == null) {
      getInstance(context)
    }
    return downloadCache?.keys?.size
  }

  @JvmStatic
  fun clearCache(context: Context) {
    if (downloadCache == null) {
      getInstance(context)
    }
    downloadCache?.keys?.forEach {
      CacheUtil.remove(downloadCache, it)
    }
  }

  /***
   *
   * ****************************************************************************************
   * Note: Not is Use! We can't rely on exoplayer's offline cache for now. Need more research
   * on this.
   ******************************************************************************************
   *
   * Let say, diff = RequestLength - BytesAlreadyCached
   * There are also chances where exoplayer show size diff in bytes even for the completely cached files
   * We have to ignore those cases and strictly follow diff == 0 then only its completely cached
   *
   * See  #CacheUtil.getCached() #CacheUtil.getRequestLength() for more.
   *
   *
   * @return true or false is VideoCache has cached all the meta data for the cache key
   */
  fun isKeyCompletelyCached(context: Context, cacheKey: String?): Boolean? {
    if (downloadCache == null) {
      getInstance(context)
    }
    val cacheFileDetails: NavigableSet<CacheSpan>? = downloadCache?.getCachedSpans(cacheKey)
    Log.d(TAG, "isKeyCompletelyCached Number of files: ${cacheFileDetails?.size}")
    val dataSpec = DataSpec(
      null,
      0,
      C.LENGTH_UNSET.toLong(),
      cacheKey,
      0
    )

    /*Pair<RequestLength in Long, BytesAlreadyCached in Long)*/
    val cachedData: Pair<Long?, Long?> = CacheUtil.getCached(dataSpec, downloadCache, null)
    val sizeDiff = cachedData.first!!.minus(cachedData.second!!.toLong())
    Log.d(TAG, "isKeyCompletelyCached: $sizeDiff")
    if (sizeDiff == 0L) {
      return true
    }
    return false
  }

  /**
   * ****************************************************************************************
   * Note: Not is Use! We can't rely on exoplayer's offline cache for now. Need more research
   * on this.
   ******************************************************************************************
   *
   * Use this function to save the cache to file. This function need a separate Thread
   *
   * Read More about #CacheDataSource #CacheDataSink #DataSpec
   *
   * @param targetFile destination of the downloaded file
   *
   * Ref: https://stackoverflow.com/questions/53692452/how-to-download-a-video-while-playing-it-using-exoplayer/55260753#55260753
   */
  fun saveToFile(
    context: Context, cacheKey: String?, targetFile: File?
  ): Boolean {

    if (downloadCache == null) {
      getInstance(context)
    }
    var isSuccessful = false
    val upstreamDataSource = DefaultHttpDataSourceFactory("Exoplayer").createDataSource()
    val dataSource = CacheDataSource(
      downloadCache,
      upstreamDataSource,
      CacheDataSourceFactory(
        downloadCache,
        DefaultHttpDataSourceFactory("Exoplayer")
      ).createDataSource(),
      /*WriteDataSink */ null,
      /* flags= */ 0,
      /* eventListener= */ null
    )

    var outFile: FileOutputStream? = null
    var bytesRead = 0

    // Total bytes read is the sum of these two variables.
    totalBytesToRead = C.LENGTH_UNSET.toLong()
    bytesReadFromCache = 0
    bytesReadFromNetwork = 0

    try {
      outFile = FileOutputStream(targetFile)
      val dataSpec = DataSpec(
        null,
        0,
        C.LENGTH_UNSET.toLong(),
        cacheKey,
        0
      )
      totalBytesToRead = dataSource.open(dataSpec)
      val data = ByteArray(1024)
      while (bytesRead != C.RESULT_END_OF_INPUT) {
        bytesRead = dataSource.read(data, 0, data.size)
        if (bytesRead != C.RESULT_END_OF_INPUT) {
          outFile.write(data, 0, bytesRead)
        }
      }
      isSuccessful = true
    } catch (e: IOException) {
      Log.e(TAG, "IOException at downloadFileUsingVideoCache: Error Processing")
    } finally {
      dataSource.close()
      outFile?.flush()
      outFile?.close()
    }

    return isSuccessful
  }

  private fun getAvailableInternalStorage(): Long {
    var availableSpace = -1L
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    availableSpace = stat.availableBlocksLong * stat.blockSizeLong
    return availableSpace
  }
}