package com.example.boombox.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.boombox.data.network.BoomboxApi

open class BaseViewModel(boomboxApi: BoomboxApi, application: Application) :
  AndroidViewModel(application) {
}
