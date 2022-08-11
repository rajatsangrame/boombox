package com.example.boombox.data.network

import androidx.lifecycle.MutableLiveData
import com.example.boombox.data.model.Track
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoomboxRepository @Inject constructor(private val api: TracksApi) {

  private val trackLiveData: MutableLiveData<List<Track>> = MutableLiveData()

  fun getTracks() = trackLiveData

  suspend fun searchTrack(query: String) {
    val result = api.searchTrack(query)
    if (result.body()?.results != null) {
      trackLiveData.value = result.body()?.results
    }
  }
}
