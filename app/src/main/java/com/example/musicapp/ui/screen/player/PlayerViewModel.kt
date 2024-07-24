package com.example.musicapp.ui.screen.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.extractor.DefaultExtractorsFactory
import com.example.musicapp.data.model.MusicControllerUiState
import com.example.musicapp.data.model.Track
import com.example.musicapp.data.repository.TracksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient


class PlayerViewModel(private val tracksRepository: TracksRepository) : ViewModel() {
    var musicControllerUiState by mutableStateOf(MusicControllerUiState())
    private val _playlist = MutableStateFlow<List<Track>?>(null)
    val playlist: StateFlow<List<Track>?> = _playlist

    private val _track = MutableStateFlow<Track?>(null)
    val track: StateFlow<Track?> = _track

    private val _isPrepared = MutableStateFlow<Boolean>(false)
    val isPrepared: StateFlow<Boolean> = _isPrepared

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _totalDuration = MutableStateFlow(0L)
    val totalDuration: StateFlow<Long> = _totalDuration

    suspend fun getPlaylist(trackId: String) {
        val fetchedPlaylist =  tracksRepository.getAllItemsStream().first()
        _playlist.value = fetchedPlaylist

        val fetchedTrack = fetchedPlaylist.find { it.id == trackId }
        fetchedTrack?.let {
            _track.value = it
        }
    }


}