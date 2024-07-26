package com.example.musicapp.ui.screen.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.musicapp.data.model.Track
import com.example.musicapp.data.repository.TracksRepository
import com.example.musicapp.service.MusicService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first


class PlayerViewModel(private val tracksRepository: TracksRepository) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private lateinit var musicService: MusicService
    private var mBound: Boolean = false

    private val _playlist = MutableStateFlow<List<Track>?>(null)
    val playlist: StateFlow<List<Track>?> = _playlist

    private val _track = MutableStateFlow<Track?>(null)
    val track: MutableStateFlow<Track?> = _track

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    val mediaPlayer : StateFlow<MediaPlayer?> = _mediaPlayer

    private var serviceConnection: ServiceConnection? = null

    fun bindService(context: Context) {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val binder = service as MusicService.MusicServiceBinder
                musicService = binder.getService()
                mBound = true
                _mediaPlayer.value = musicService.getPlayer()
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                mBound = false
                _mediaPlayer.value = null
            }

        }
        serviceConnection = connection
        Intent(context, MusicService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unBindService(context: Context){
        serviceConnection?.let {
            context.unbindService(it)
            serviceConnection = null
        }
    }

    suspend fun loadPlaylistAndTrack(trackId: String) {
        val fetchedPlaylist = tracksRepository.getAllItemsStream().first()
        _playlist.value = fetchedPlaylist

        val fetchedTrack = fetchedPlaylist.find { it.id == trackId }
        fetchedTrack?.let {
            _track.value = it
        }
    }

    fun seekTo(context: Context, position: Int){
        val intent = Intent(context, MusicService::class.java).apply {
            action = MusicService.Action.SEEK_TO.toString()
            putExtra(MusicService.EXTRA_POSITION, position)
        }
        context.startService(intent)
    }
    fun prepareTrack(context: Context){
        val intent = Intent(context, MusicService::class.java).apply {
            action = MusicService.Action.PREPARE.toString()
            putExtra(MusicService.EXTRA_TRACK_URL, _track.value?.preview)
            putExtra(MusicService.EXTRA_IS_PLAYING, _isPlaying.value)
        }
        context.startService(intent)
    }

    fun playOrPause(context: Context){
        val intent = Intent(context, MusicService::class.java).apply {
            action = if (_isPlaying.value) MusicService.Action.PAUSE.toString() else MusicService.Action.PLAY.toString()
            putExtra(MusicService.EXTRA_TRACK_URL, _track.value?.preview)
        }
        context.startService(intent)
        _isPlaying.value = !_isPlaying.value
    }

}