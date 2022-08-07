package com.example.boombox.data.network

import androidx.lifecycle.MutableLiveData
import com.example.boombox.data.model.Track
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoomboxRepository @Inject constructor(private val api: BoomboxApi) {

  private val trackLiveData: MutableLiveData<List<Track>> = MutableLiveData()

  fun getTracks() = trackLiveData

  suspend fun searchQuerySingle(query: String) {
    val result = api.searchQuerySingle(query)
    if (result.body()?.results != null) {
      trackLiveData.value = result.body()?.results
    }
  }
}
