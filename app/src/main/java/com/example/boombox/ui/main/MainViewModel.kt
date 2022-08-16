package com.example.boombox.ui.main

import android.app.Application
import androidx.lifecycle.liveData
import com.example.boombox.data.network.BoomboxRepository
import com.example.boombox.data.network.Resource
import com.example.boombox.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow

import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val repository: BoomboxRepository,
  application: Application
) : BaseViewModel(repository, application) {

  fun getTracks(q: String = "taylor swift") = liveData(Dispatchers.IO) {
    emit(Resource.loading(data = null))
    try {
      emit(Resource.success(data = repository.getTracks(q)))
    } catch (exception: Exception) {
      emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
    }
  }
}
