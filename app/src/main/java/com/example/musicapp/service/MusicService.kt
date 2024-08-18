package com.example.musicapp.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.musicapp.R
import java.io.IOException


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

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CreateNotification.CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for music service notifications"
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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
                intent.getBooleanExtra(EXTRA_IS_PLAYING, false),
                intent.getStringExtra(EXTRA_TRACK_NAME),
                intent.getStringExtra(EXTRA_ARTIST_NAME)
            )
        }
        return START_NOT_STICKY
    }

    private fun play() {
        if (isPrepared.value) {
            CreateNotification.createNotification(
                this, null, null,
                R.drawable.ic_pause_black_24dp
            )
            startForegroundServiceWithNotification()
            player.start()
        } else {
            Log.e("MusicService", "Player is not prepared yet.")
        }
    }

    private fun prepare(
        trackUrl: String?,
        isPlaying: Boolean,
        trackName: String?,
        artistName: String?
    ) {
        if (trackUrl == null) {
            Log.e("MusicService", "Track URL is null, cannot prepare the player.")
            return
        }
        try {
            Log.d("MusicService", "Preparing player with URL: $trackUrl")
            player.reset()
            player.setDataSource(trackUrl)
            player.prepareAsync()
            player.setOnPreparedListener {
                isPrepared.value = true
                Log.d("MusicService", "Player prepared successfully.")
                if (isPlaying) {
                    player.start()
                    Log.d("MusicService", "Player started playing.")
                }
            }
            if (trackName != null && artistName != null) {
                CreateNotification.createNotification(
                    this, trackName, artistName,
                    if (isPlaying) R.drawable.ic_pause_black_24dp else R.drawable.ic_play_arrow_black_24dp
                )
                startForegroundServiceWithNotification()
            }
        } catch (e: IOException) {
            Log.e("MediaPlayer", "Error setting data source or preparing", e)
        }
    }

    private fun seekTo(position: Int) {
        if (isPrepared.value) {
            player.seekTo(position)
        } else {
            Log.e("MusicService", "Player is not prepared, cannot seek.")
        }
    }

    private fun pause() {
        if (player.isPlaying) {
            CreateNotification.createNotification(
                this, null, null,
                R.drawable.ic_play_arrow_black_24dp
            )
            startForegroundServiceWithNotification()
            player.pause()
        }
    }

    private fun stop() {
        if (isPrepared.value) {
            player.stop()
            stopForeground(true)
            stopSelf()
        }
    }

    override fun onDestroy() {
        stop()
        player.release()
        super.onDestroy()
    }

    private fun startForegroundServiceWithNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CreateNotification.notification?.let {
                if (Build.VERSION.SDK_INT >= 34) {
                    startForeground(1, it, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
                } else {
                    startForeground(1, it)
                }
            }
        }
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
        const val EXTRA_TRACK_IMAGE = "EXTRA_TRACK_IMAGE"
        const val EXTRA_TRACK_URL = "EXTRA_TRACK_URL"
        const val EXTRA_POSITION = "EXTRA_POSITION"
        const val EXTRA_IS_PLAYING = "EXTRA_IS_PLAYING"
        const val EXTRA_TRACK_NAME = "EXTRA_TRACK_NAME"
        const val EXTRA_ARTIST_NAME = "EXTRA_ARTIST_NAME"
    }
}