package com.example.boombox.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.boombox.data.model.Track
import com.example.boombox.data.network.BoomboxApi
import com.example.boombox.extension.enqueue
import com.example.boombox.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val boomboxApi: BoomboxApi,
  application: Application
) : BaseViewModel(boomboxApi, application) {

  private val trackList: MutableLiveData<List<Track>> = MutableLiveData()

  fun getTracks() = trackList

  fun search(q: String = "taylor swift") {
    boomboxApi.searchQuerySingle(q).enqueue(
      success = {
        trackList.value = it.body()?.results
      }, failure = {
        Log.e(TAG, "search: failure")
      }, error = {
        Log.e(TAG, "search: error")
      })
  }

  companion object {
    private const val TAG = "MainViewModel"
  }
}
