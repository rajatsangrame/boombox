package com.example.boombox.data.network

import com.example.boombox.data.model.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TracksApi {

  @GET("search")
  suspend fun searchTrack(@Query("term") query: String): TrackResponse
}
