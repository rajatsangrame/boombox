package com.example.boombox.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.boombox.data.network.BoomboxApi
import com.example.boombox.data.network.BoomboxRepository

open class BaseViewModel(repository: BoomboxRepository, application: Application) :
  AndroidViewModel(application) {
}
