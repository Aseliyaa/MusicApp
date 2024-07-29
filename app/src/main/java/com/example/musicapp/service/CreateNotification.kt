package com.example.musicapp.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.widget.RemoteViews
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import com.example.musicapp.R
import kotlinx.coroutines.flow.MutableStateFlow


class CreateNotification {
    enum class Action {
        PLAY_OR_PAUSE, PREVIOUS, NEXT
    }

    companion object {
        const val EXTRA_ACTION_NAME = "EXTRA_ACTION_NAME"
        const val ACTION = "ACTION"
        const val CHANNEL_ID = "channelID"
        private val track = MutableStateFlow<String?>(null)
        private val artist = MutableStateFlow<String?>(null)
        var notification: Notification? = null

        @SuppressLint("ResourceAsColor")
        fun createNotification(
            context: Context,
            trackName: String?,
            artistName: String?,
            intentPlayDrw: Int
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val drw_previous = R.drawable.ic_skip_previous_black_24dp
                val drw_next = R.drawable.ic_skip_next_black_24dp

                if (trackName != null && artistName != null) {
                    track.value = trackName
                    artist.value = artistName
                }

                val intentPrevious = Intent(context, NotificationReceiver::class.java).apply {
                    action = Action.PREVIOUS.toString()
                }
                val pendingIntentPrevious = PendingIntent.getBroadcast(
                    context,
                    0,
                    intentPrevious,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )


                val intentPlay = Intent(context, NotificationReceiver::class.java).apply {
                    action = Action.PLAY_OR_PAUSE.toString()
                }
                val pendingIntentPlay = PendingIntent.getBroadcast(
                    context,
                    0,
                    intentPlay,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )


                val intentNext = Intent(context, NotificationReceiver::class.java).apply {
                    action = Action.NEXT.toString()
                }
                val pendingIntentNext = PendingIntent.getBroadcast(
                    context,
                    0,
                    intentNext,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val mediaSessionCompat = MediaSessionCompat(context, "tag")
                notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music_note)
                    .setColor(context.getColor(R.color.notification_color))
                    .setContentTitle(track.value)
                    .setContentText(artist.value)
                    .setOngoing(true)
                    .setShowWhen(false)
                    .addAction(drw_previous, "Previous", pendingIntentPrevious)
                    .addAction(intentPlayDrw, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(
                        androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.sessionToken)
                    )
                    .build()
            }
        }
    }
}