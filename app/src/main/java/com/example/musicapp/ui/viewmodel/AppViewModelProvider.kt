package com.example.musicapp.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musicapp.MusicApplication
import com.example.musicapp.ui.screen.home.HomeViewModel
import com.example.musicapp.ui.screen.player.PlayerViewModel
import com.example.musicapp.ui.screen.welcome.WelcomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            WelcomeViewModel()
        }
        initializer {
            HomeViewModel(
                musicApplication().container.genresRepository,
                musicApplication().container.artistsRepository,
                musicApplication().container.tracksRepository
            )
        }
        initializer {
            PlayerViewModel(
                musicApplication().container.tracksRepository
            )
        }
    }
}


fun CreationExtras.musicApplication(): MusicApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MusicApplication)
