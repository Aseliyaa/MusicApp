package com.example.musicapp.data.model

data class MusicControllerUiState(
    val isPlaying: Boolean = false,
    var currentPosition: Long = 0L,
    var sliderPosition: Long = 0L,
    var totalDuration: Long = 0L
)
