package com.example.boombox.media

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.annotation.Nullable
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File
import com.example.boombox.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerManager @Inject constructor(private val context: Context) {

  private var player: SimpleExoPlayer? = null
  private var currentMedia: Media? = null
  private var notificationManager: PlayerNotificationManager? = null
  private val simpleCache: SimpleCache by lazy {
    AudioCache.getInstance(context)
  }

  companion object {
    private const val TAG = "AudioPlayerManager"
  }

  fun setMedia(media: Media) {
    currentMedia = media
  }

  fun initAndPlay() {
    initExoPlayer()
    preparePlayer()
    play()
  }

  fun getPlayer() = player

  private fun initExoPlayer() {

    // Stop existing song if playing
    stop()

    player = ExoPlayerFactory.newSimpleInstance(context)
    // Setting for handling the audio focus if any third playing app is playing audio
    // This will automatically done on OS level
    val audioAttributes: AudioAttributes = AudioAttributes.Builder()
      .setUsage(C.USAGE_MEDIA)
      .setContentType(C.CONTENT_TYPE_MUSIC)
      .build()
    player?.setAudioAttributes(audioAttributes, true) // handleAudioFocus = true, mind the case here
    player?.addListener(eventListener)
    notificationManager = PlayerNotificationManager.createWithNotificationChannel(
      context,
      "GENERAL_CHANNEL",
      R.string.app_name,
      R.string.app_name,
      55, adapter
    )
    notificationManager?.setPlayer(player)
  }

  /**
   * This function will prepare the [DataSource] according the local url or remote url
   */
  private fun preparePlayer() {

    if (currentMedia?.localUrl == null) {
      player?.prepare(getCacheMediaSource(currentMedia?.remoteUrl, currentMedia?.cacheKey))
    } else {
      player?.prepare(getMediaSourceForLocalFiles(currentMedia?.localUrl))
    }
    player?.playWhenReady = false /* when player.prepare is called it starts playing*/

  }

  fun isPlayingSameTrack(id: Int) = currentMedia?.id == id

  fun play() {
    currentMedia?.isFinished = false
    currentMedia?.isPausedByUser = false
    player?.playWhenReady = true
  }

  fun pause() {
    currentMedia?.isFinished = false
    currentMedia?.isPausedByUser = true
    player?.playWhenReady = false
  }

  private fun getMediaSourceForLocalFiles(localUrl: String?): MediaSource? {
    val f = File(localUrl)
    val uri = Uri.fromFile(f)
    val dataSourceFactory: DataSource.Factory = FileDataSourceFactory()
    return ProgressiveMediaSource.Factory(
      dataSourceFactory
    )
      .createMediaSource(uri)
  }

  /**
   * @param mediaUrl : Http url of the media
   * @param cacheKey : If empty or null this method returns getDefaultMediaSource
   *
   * @return MediaSource for ExoPlayer with cache
   */
  private fun getCacheMediaSource(
    mediaUrl: String?,
    cacheKey: String?
  ): MediaSource? {

    if (cacheKey == null || cacheKey.isEmpty()) {
      return getDefaultMediaSource(mediaUrl)
    }

    val cacheDataSourceFactory =
      CacheDataSourceFactory(simpleCache, DefaultHttpDataSourceFactory("Exoplayer"))
    val uri = Uri.parse(mediaUrl)
    val factory = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
    factory.setCustomCacheKey(cacheKey)
    return factory.createMediaSource(uri)
  }

  private fun getDefaultMediaSource(mediaUrl: String?): MediaSource? {

    val uri = Uri.parse(mediaUrl)
    val factory = ProgressiveMediaSource.Factory(
      DefaultDataSourceFactory(
        context, context.getString(
          R.string.app_name
        )
      )
    )
    return factory.createMediaSource(uri)
  }

  private fun stop() {
    player?.stop()
    releasePlayer()
  }

  private fun releasePlayer() {
    if (player != null) {
      player?.release()
      player = null
      notificationManager?.setPlayer(null)
    }
  }

  private val adapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): String {
      return currentMedia?.name ?: "Media"
    }

    @Nullable
    override fun createCurrentContentIntent(player: Player): PendingIntent? {
      return null
      // TODO: This will be used to jump on current playing chatroom on notification click
      /*
      return PendingIntent.getActivity(
              context,
              0,
              Intent(context, ExoplayerActivity::class.java),
              PendingIntent.FLAG_UPDATE_CURRENT)
       */
    }

    @Nullable
    override fun getCurrentContentText(player: Player): String? {
      return null
    }

    @Nullable
    override fun getCurrentLargeIcon(
      player: Player,
      callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
      return null
    }
  }

  private val eventListener: Player.EventListener = object : Player.EventListener {
    override fun onTimelineChanged(
      timeline: Timeline,
      manifest: Any?,
      reason: Int
    ) {
    }

    override fun onLoadingChanged(isLoading: Boolean) {
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
    }

    override fun onPositionDiscontinuity(reason: Int) {
    }

    override fun onPlayerError(error: ExoPlaybackException) {
      stop()
    }

    override fun onPlayerStateChanged(
      playWhenReady: Boolean,
      playbackState: Int
    ) {
      Log.d(TAG, "eventListener: onPlayerStateChanged: ")
      if (playbackState == Player.STATE_BUFFERING) {
      } else {
      }
      if (playbackState == Player.STATE_ENDED) {
        Log.d(TAG, "eventListener: onPlayerStateChanged: Player.STATE_ENDED")
      }
    }
  }

  data class Media(
    val id: Int,
    val name: String,
    val localUrl: String?,
    val remoteUrl: String,
    val cacheKey: String,
    var isPausedByUser: Boolean,
    var isFinished: Boolean
  )

}