package com.example.boombox.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.boombox.data.model.Track
import com.example.boombox.data.network.BoomboxApi
import com.example.boombox.data.network.BoomboxRepository
import com.example.boombox.extension.enqueue
import com.example.boombox.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val repository: BoomboxRepository,
  application: Application
) : BaseViewModel(repository, application) {

  fun getTracks() = repository.getTracks()

  fun search(q: String = "taylor swift") {
    viewModelScope.launch {
      repository.searchQuerySingle(q)
    }
  }
}
