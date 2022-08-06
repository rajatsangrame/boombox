package com.example.boombox.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.boombox.R
import com.example.boombox.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {

  private val mainViewModel by activityViewModels<MainViewModel>()
  private lateinit var binding: HomeFragmentBinding

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding = HomeFragmentBinding.bind(view)
  }
}
