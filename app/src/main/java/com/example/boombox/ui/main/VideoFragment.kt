package com.example.boombox.ui.main

import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import com.example.boombox.databinding.AudioFragmentBinding
import com.example.boombox.ui.adapter.TrackAdapter
import com.example.boombox.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoFragment : BaseFragment<AudioFragmentBinding>() {

  private val mainViewModel by activityViewModels<MainViewModel>()
  private lateinit var trackAdapter: TrackAdapter

  override fun getViewBinding(inflater: LayoutInflater): AudioFragmentBinding {
    return AudioFragmentBinding.inflate(inflater)
  }

  override fun setupObserver() {

  }

  override fun setupView() {

  }
}