package com.example.boombox.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.boombox.R
import com.example.boombox.data.model.Track
import com.example.boombox.databinding.TrackItemBinding
import com.example.boombox.extension.loadImage
import com.example.boombox.media.AudioPlayerManager

class TrackAdapter(
  private var trackList: List<Track> = ArrayList(),
  private val audioPlayerManager: AudioPlayerManager,
  private val listener: (Track) -> Unit
) :
  RecyclerView.Adapter<TrackAdapter.ViewHolder>() {

  fun setList(trackList: List<Track>) {
    this.trackList = trackList
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackAdapter.ViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = TrackItemBinding.inflate(
      layoutInflater,
      parent, false
    )
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: TrackAdapter.ViewHolder, position: Int) {
    holder.bind(trackList[position])
  }

  override fun getItemCount(): Int {
    return trackList.size
  }

  inner class ViewHolder(val binding: TrackItemBinding) : RecyclerView.ViewHolder(binding.root) {

    init {
      binding.btnPlay.setOnClickListener {
        listener(trackList[adapterPosition])
      }
    }

    fun bind(track: Track) {
      val context = itemView.context
      val drawable = if (!audioPlayerManager.isPlayingSameTrack(track.trackId)) {
        ContextCompat.getDrawable(context, R.drawable.ic_play)
      } else {
        ContextCompat.getDrawable(context, R.drawable.ic_stop)
      }
      binding.ivImage.loadImage(track.artworkUrl100!!)
      binding.btnPlay.setImageDrawable(drawable)
      binding.tvTitle.text = track.trackName
      binding.tvArtist.text = track.artistName

    }
  }

}
