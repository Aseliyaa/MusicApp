package com.example.musicapp

import android.app.Application
import com.example.musicapp.data.AppContainer
import com.example.musicapp.data.AppDataContainer

class MusicApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}