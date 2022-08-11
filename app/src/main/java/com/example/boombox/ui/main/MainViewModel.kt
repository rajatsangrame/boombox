package com.example.boombox.ui.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.boombox.data.network.BoomboxRepository
import com.example.boombox.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val repository: BoomboxRepository,
  application: Application
) : BaseViewModel(repository, application) {

  fun getTracks() = repository.getTracks()

  fun search(q: String = "taylor swift") {
    viewModelScope.launch {
      repository.searchTrack(q)
    }
  }
}
