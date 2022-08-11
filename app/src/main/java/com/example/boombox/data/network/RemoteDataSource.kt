package com.example.boombox.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Inject

class RemoteDataSource @Inject constructor() {
  companion object {
    const val TRACK_BASE_URL: String = "https://itunes.apple.com/"
    const val VIDEO_BASE_URL: String = "https://api.pexels.com/videos/"

  }

  fun buildTracksApi(): TracksApi {
    return Retrofit.Builder()
      .baseUrl(TRACK_BASE_URL)
      .client(getRetrofitClient())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(TracksApi::class.java)
  }

  fun buildVideoTracksApi(): TracksApi {
    return Retrofit.Builder()
      .baseUrl(VIDEO_BASE_URL)
      .client(getRetrofitClient())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(TracksApi::class.java)
  }

  private fun getRetrofitClient(
  ): OkHttpClient {
    return OkHttpClient()
      .newBuilder()
      .addInterceptor(interceptor())
      .addInterceptor(loggingInterceptor())
      .build()
  }

  private fun interceptor(): Interceptor {
    return object : Interceptor {
      @Throws(IOException::class)
      override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url
        val url = originalHttpUrl.newBuilder()
          .build()
        val requestBuilder = original.newBuilder()
          .url(url)
        val request = requestBuilder.build()
        return chain.proceed(request)
      }
    }
  }

  private fun loggingInterceptor(): HttpLoggingInterceptor {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    return logging
  }
}
