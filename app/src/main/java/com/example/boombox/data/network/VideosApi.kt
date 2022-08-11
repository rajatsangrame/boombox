package com.example.boombox.data.network

import com.example.boombox.data.model.TrackResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VideosApi {

  @GET("search")
  suspend fun searchVideos(
    @Query("query") query: String,
    @Query("per_page") page: Int
  ): Response<TrackResponse>
}
