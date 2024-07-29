package com.example.musicapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.sendBroadcast(Intent(CreateNotification.ACTION)
            .putExtra(CreateNotification.EXTRA_ACTION_NAME, intent.action));
    }
}