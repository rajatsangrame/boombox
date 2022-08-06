package com.example.boombox.ui.main

import android.app.Application
import android.util.Log
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

  fun search(q: String = "taylor swift") {
    boomboxApi.searchQuerySingle(q).enqueue(
      success = {
      Log.d(TAG, "search: success")
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
