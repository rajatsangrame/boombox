package com.example.boombox.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.boombox.R
import com.example.boombox.data.model.Track
import com.example.boombox.databinding.HomeFragmentBinding
import com.example.boombox.media.AudioPlayerManager
import com.example.boombox.ui.adapter.TrackAdapter
import com.example.boombox.util.GridItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {

  private val mainViewModel by activityViewModels<MainViewModel>()
  private lateinit var binding: HomeFragmentBinding
  private lateinit var trackAdapter: TrackAdapter
  @Inject lateinit var audioPlayerManager: AudioPlayerManager

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding = HomeFragmentBinding.bind(view)

    setupUI()
    mainViewModel.search()
  }

  private fun setupUI() {
    setupRecyclerView()
    setupObserver()
  }

  private fun setupRecyclerView() {
    trackAdapter =
      TrackAdapter(trackList = ArrayList(), audioPlayerManager = audioPlayerManager) { track ->
        handlePlayback(track)
      }
    binding.rvTracks.apply {
      layoutManager = GridLayoutManager(context, 2)
      addItemDecoration(GridItemDecorator(2, 50, true))
      adapter = trackAdapter
    }
  }

  private fun setupObserver() {
    mainViewModel.getTracks().observe(this, {
      it?.let {
        trackAdapter.setList(it)
      }
    })
  }

  private fun handlePlayback(track: Track) {
    if (!audioPlayerManager.isPlayingSameTrack(track.trackId)) {
      audioPlayerManager.setMedia(track.toMedia())
      audioPlayerManager.initAndPlay()
    } else {
      audioPlayerManager.pause()
    }
    trackAdapter.notifyDataSetChanged()
  }
}
