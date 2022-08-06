package com.example.boombox.data.model

import com.google.gson.annotations.SerializedName

class BoomboxResponse(
  @field:SerializedName("resultCount")
  val resultCount: Int,

  @field:SerializedName("results")
  val results: List<Track>
)
