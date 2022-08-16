package com.example.boombox.data.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoomboxRepository @Inject constructor(private val api: TracksApi) {

  suspend fun getTracks(query: String) = api.searchTrack(query)
}
