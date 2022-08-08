package com.example.boombox.ui.main

import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.boombox.R
import com.example.boombox.data.model.Track
import com.example.boombox.databinding.HomeFragmentBinding
import com.example.boombox.media.AudioPlayerManager
import com.example.boombox.ui.adapter.TrackAdapter
import com.example.boombox.util.GridItemDecorator
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
    fetchData()
  }

  private fun setupUI() {
    setupRecyclerView()
    setupObserver()
  }

  private fun fetchData() {
    mainViewModel.search()
  }

  private fun setupRecyclerView() {
    trackAdapter =
      TrackAdapter(
        trackList = ArrayList(),
        audioPlayerManager = audioPlayerManager,
        listener = { track ->
          handlePlayback(track)
        },
        menuClickListener = { track, view ->
          createOptionMenu(track, view)
        })
    binding.rvTracks.apply {
      layoutManager = GridLayoutManager(context, 2)
      addItemDecoration(GridItemDecorator(2, 32, true))
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

  private fun createOptionMenu(track: Track, view: View) {
    val popup = PopupMenu(requireContext(), view)
    val inflater: MenuInflater = popup.menuInflater
    inflater.inflate(R.menu.menu_track, popup.menu)
    popup.show()
    popup.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.add_to_playlist -> {

          return@setOnMenuItemClickListener false
        }
      }
      return@setOnMenuItemClickListener false
    }
  }

  private fun handlePlayback(track: Track) {
    val isCurrentTrack = audioPlayerManager.isPlayingSameTrack(track.trackId)
    if (!isCurrentTrack) {
      audioPlayerManager.setMedia(track.toMedia {
        when (it) {
          AudioPlayerManager.STATE_PAUSE -> {
            track.isPlaying = false
          }
          AudioPlayerManager.STATE_PLAY -> {
            track.isPlaying = true
          }
        }
        trackAdapter.notifyDataSetChanged()
      })
      audioPlayerManager.initAndPlay()
      track.isPlaying = true
    } else if (isCurrentTrack && (audioPlayerManager.isPausedByUser() || !audioPlayerManager.isPlaying())) {
      audioPlayerManager.play()
      track.isPlaying = true
    } else {
      audioPlayerManager.pause()
      track.isPlaying = false
    }
    trackAdapter.notifyDataSetChanged()
  }
}
