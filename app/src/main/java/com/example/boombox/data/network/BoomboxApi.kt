package com.example.boombox.data.network

import com.example.boombox.data.model.BoomboxResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BoomboxApi {

  @GET("search")
  suspend fun searchQuerySingle(@Query("term") query: String): Response<BoomboxResponse>
}
