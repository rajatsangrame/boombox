package com.example.boombox.data.network

import com.example.boombox.data.model.BoomboxResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BoomboxApi {

  @GET("search")
  fun searchQuerySingle(@Query("term") query: String): Call<BoomboxResponse>
}
