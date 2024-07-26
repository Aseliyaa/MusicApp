package com.example.musicapp.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableStateOf

class MusicService : Service() {
    private lateinit var player: MediaPlayer
    private val binder = MusicServiceBinder()
    private var isPrepared = mutableStateOf(false)

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.PLAY.toString() -> play()
            Action.STOP.toString() -> stop()
            Action.PAUSE.toString() -> pause()
            Action.SEEK_TO.toString() -> seekTo(intent.getIntExtra(EXTRA_POSITION, 0))
            Action.PREPARE.toString() -> prepare(
                    intent.getStringExtra(EXTRA_TRACK_URL),
                    intent.getBooleanExtra(EXTRA_IS_PLAYING, false)
                )
        }
        return START_NOT_STICKY
    }

    private fun play() {
        if (isPrepared.value)
            player.start()
    }

    private fun prepare(trackUrl: String?, isPlaying: Boolean) {
        Log.d("METHOD", "prepare")
        try {
            trackUrl?.let {
                player.reset()
                player.setDataSource(it)
                player.prepareAsync()
                player.setOnPreparedListener {
                    isPrepared.value = true
                    if (isPlaying) {
                        player.start()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error setting data source or preparing", e)
        }
    }


    private fun seekTo(position: Int) {
        player.seekTo(position)
    }

    private fun pause() {
        if (player.isPlaying)
            player.pause()
    }

    private fun stop() {
        if (player.isPlaying)
            player.stop()
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    fun getPlayer(): MediaPlayer {
        return player
    }

    enum class Action {
        SEEK_TO,
        PLAY,
        PAUSE,
        STOP,
        PREPARE
    }

    companion object {
        const val EXTRA_TRACK_URL = "EXTRA_TRACK_URL"
        const val EXTRA_POSITION = "EXTRA_POSITION"
        const val EXTRA_IS_PLAYING = "EXTRA_IS_PLAYING"
    }
}
