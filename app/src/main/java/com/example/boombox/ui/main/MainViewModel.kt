package com.example.boombox.ui.main

import android.app.Application
import androidx.lifecycle.asLiveData
import com.example.boombox.data.network.BoomboxRepository
import com.example.boombox.data.network.Resource
import com.example.boombox.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow

import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val repository: BoomboxRepository,
  application: Application
) : BaseViewModel(repository, application) {

  fun getTracks(q: String = "taylor swift") = flow {
    emit(Resource.loading(data = null))
    try {
      emit(Resource.success(data = repository.getTracks(q)))
    } catch (exception: Exception) {
      emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
    }
  }.asLiveData()
}
