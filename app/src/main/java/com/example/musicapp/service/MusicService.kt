package com.example.musicapp.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.musicapp.R


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
                intent.getStringExtra(EXTRA_ARTIST_NAME),
                intent.getStringExtra(EXTRA_TRACK_IMAGE)
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
            startForeground(1, CreateNotification.notification)
            player.start()
        }
    }

    private fun prepare(
        trackUrl: String?,
        isPlaying: Boolean,
        trackName: String?,
        artistName: String?,
        imageUri: String?
    ) {
        try {
            trackUrl?.let {
                if (trackName != null && artistName != null) {
                    CreateNotification.createNotification(
                        this, trackName, artistName,
                        if (isPlaying) R.drawable.ic_pause_black_24dp else R.drawable.ic_play_arrow_black_24dp
                    )
                    startForeground(1, CreateNotification.notification)
                }
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
        if (player.isPlaying) {
            CreateNotification.createNotification(
                this, null, null,
                R.drawable.ic_play_arrow_black_24dp
            )
            startForeground(1, CreateNotification.notification)
            player.pause()
        }
    }

    private fun stop() {
        player.stop()
        stopSelf()
    }

    override fun onDestroy() {
        stop()
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
        const val EXTRA_TRACK_IMAGE = "EXTRA_TRACK_IMAGE"
        const val EXTRA_TRACK_URL = "EXTRA_TRACK_URL"
        const val EXTRA_POSITION = "EXTRA_POSITION"
        const val EXTRA_IS_PLAYING = "EXTRA_IS_PLAYING"
        const val EXTRA_TRACK_NAME = "EXTRA_TRACK_NAME"
        const val EXTRA_ARTIST_NAME = "EXTRA_ARTIST_NAME"
    }
}