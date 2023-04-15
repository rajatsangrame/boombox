package com.example.boombox.media

import android.app.PendingIntent
import com.example.boombox.R
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.Nullable
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AudioPlayerManager @Inject constructor(private val context: Context) {

  private var player: ExoPlayer? = null
  private var currentMedia: Media? = null
  private var notificationManager: PlayerNotificationManager? = null

  companion object {
    private const val TAG = "AudioPlayerManager"
    const val STATE_IDLE = Player.STATE_IDLE //-> 1
    const val STATE_BUFFERING = Player.STATE_BUFFERING //-> 2
    const val STATE_READY = Player.STATE_READY //-> 3
    const val STATE_ENDED = Player.STATE_ENDED //-> 4
    const val STATE_PLAY = 5
    const val STATE_PAUSE = 6
  }

  fun setMedia(media: Media) {
    currentMedia = media
  }

  fun initAndPlay() {
    initExoPlayer()
    preparePlayer()
    play()
  }

  private fun initExoPlayer() {

    // Stop existing song if playing
    stop()

    player = ExoPlayer.Builder(context)
      .build()

    // Setting for handling the audio focus if any third playing app is playing audio
    // This will automatically done on OS level
    val audioAttributes: AudioAttributes = AudioAttributes.Builder()
      .setUsage(C.USAGE_MEDIA)
      .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
      .build()
    player?.setAudioAttributes(audioAttributes, true) // handleAudioFocus = true, mind the case here
    player?.addListener(eventListener)
    notificationManager = PlayerNotificationManager.Builder(
      context,
      55,
      context.getString(R.string.app_name)
    )
      .build()
    notificationManager?.setPlayer(player)
  }

  /**
   * This function will prepare the [DataSource] according the local url or remote url
   */
  private fun preparePlayer() {

    val mediaItem = MediaItem.Builder()
      .setUri(currentMedia!!.remoteUrl)
      .setMimeType(MimeTypes.VIDEO_MP43)
      .build()

    val mediaSource = ProgressiveMediaSource.Factory(
      DefaultDataSource.Factory(context)
    ).createMediaSource(mediaItem)

    player?.apply {
      setMediaSource(mediaSource)
      seekTo(0) // Start from the beginning
      prepare() // Change the state from idle.
    }
    player?.playWhenReady = false /* when player.prepare is called it starts playing*/

  }

  fun isPlayingSameTrack(id: Int) = currentMedia?.id == id


  fun isPlaying() = player?.isPlaying ?: false

  fun play() {
    currentMedia?.state?.invoke(STATE_PLAY)
    player?.playWhenReady = true
  }

  fun pause() {
    currentMedia?.state?.invoke(STATE_PAUSE)
    player?.playWhenReady = false
  }

  private fun stop() {
    player?.stop()
    releasePlayer()
  }

  private fun releasePlayer() {
    if (player != null) {
      player?.release()
      player = null
      notificationManager?.setPlayer(null)
    }
  }

  private val adapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): String {
      return currentMedia?.name ?: "Media"
    }

    @Nullable
    override fun createCurrentContentIntent(player: Player): PendingIntent? {
      return null
      // TODO: This will be used to jump on current playing chatroom on notification click
      /*
      return PendingIntent.getActivity(
              context,
              0,
              Intent(context, ExoplayerActivity::class.java),
              PendingIntent.FLAG_UPDATE_CURRENT)
       */
    }

    @Nullable
    override fun getCurrentContentText(player: Player): String? {
      return null
    }

    @Nullable
    override fun getCurrentLargeIcon(
      player: Player,
      callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
      return null
    }
  }

  private val eventListener: Player.Listener = object : Player.Listener {

    override fun onLoadingChanged(isLoading: Boolean) {
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
      val state = if (isPlaying) STATE_PLAY else STATE_PAUSE
      currentMedia?.state?.invoke(state)
    }

    override fun onPositionDiscontinuity(reason: Int) {
    }

    override fun onPlayerError(error: PlaybackException) {
      super.onPlayerError(error)
      stop()
    }

    override fun onPlayerStateChanged(
      playWhenReady: Boolean,
      playbackState: Int
    ) {
      currentMedia?.state?.invoke(playbackState)
    }
  }

  data class Media(
    val id: Int,
    val name: String,
    val localUrl: String?,
    val remoteUrl: String,
    val cacheKey: String,
    var isFinished: Boolean,
    val state: (Int) -> Unit
  )
}