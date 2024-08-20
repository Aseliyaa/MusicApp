package com.example.musicapp.ui.screen.player

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import com.example.musicapp.data.model.Track
import com.example.musicapp.data.repository.FavoritesRepository
import com.example.musicapp.data.repository.TracksRepository
import com.example.musicapp.service.CreateNotification
import com.example.musicapp.service.MusicService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first


class PlayerViewModel(
    private val tracksRepository: TracksRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private lateinit var musicService: MusicService

    @SuppressLint("StaticFieldLeak")
    private var _context: Context? = null

    private var mBound: Boolean = false

    private val _playlist = MutableStateFlow<List<Track>?>(null)
    val playlist: StateFlow<List<Track>?> = _playlist

    private val _track = MutableStateFlow<Track?>(null)
    val track: MutableStateFlow<Track?> = _track

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    val mediaPlayer: StateFlow<MediaPlayer?> = _mediaPlayer

    private var serviceConnection: ServiceConnection? = null

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.extras!!.getString(CreateNotification.EXTRA_ACTION_NAME)) {
                CreateNotification.Action.PREVIOUS.toString() -> onTrackPrevious(context)
                CreateNotification.Action.PLAY_OR_PAUSE.toString() -> playOrPause(context)
                CreateNotification.Action.NEXT.toString() -> onTrackNext(context)
                CreateNotification.Action.NOTIFICATION_DELETE.toString() -> stop(context)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun bindService(context: Context) {
        _context = context
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
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, MusicService::class.java))

            context.registerReceiver(
                broadcastReceiver,
                IntentFilter(CreateNotification.ACTION),
                Context.RECEIVER_EXPORTED
            )
        } else {
            context.startService(Intent(context, MusicService::class.java))
            context.registerReceiver(broadcastReceiver, IntentFilter(CreateNotification.ACTION))
        }
    }

    fun unBindService(context: Context) {
        serviceConnection?.let {
            context.unregisterReceiver(broadcastReceiver)
            context.unbindService(it)
            serviceConnection = null
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun loadPlaylistAndTrack(trackId: String) {
        for(track in tracksRepository.getAllItemsStream().first()){
            if(favoritesRepository.getItemStream(track.id).first() != null){
                track.isClicked = true
                tracksRepository.updateItem(track)
            } else {
                track.isClicked = false
                tracksRepository.updateItem(track)
            }
        }

        val fetchedPlaylist = tracksRepository.getAllItemsStream().first()
        _playlist.value = fetchedPlaylist

        val fetchedTrack = fetchedPlaylist.find { it.id == trackId }
        fetchedTrack?.let {
            _track.value = it
        }
    }

    fun seekTo(context: Context, position: Int) {
        val intent = Intent(context, MusicService::class.java).apply {
            action = MusicService.Action.SEEK_TO.toString()
            putExtra(MusicService.EXTRA_POSITION, position)
        }
        context.startService(intent)
    }

    fun onTrackPrevious(context: Context) {
        val newIndex = _playlist.value?.indexOf(_track.value)?.minus(1)
        _track.value = newIndex?.let { _playlist.value?.get(it) }
        prepareTrack(context)
    }

    fun onTrackNext(context: Context){
        val newIndex = _playlist.value?.indexOf(_track.value)?.plus(1)
        _track.value = newIndex?.let { _playlist.value?.get(it) }
        prepareTrack(context)
    }
    fun prepareTrack(context: Context) {
        val intent = Intent(context, MusicService::class.java).apply {
            action = MusicService.Action.PREPARE.toString()
            putExtra(MusicService.EXTRA_TRACK_URL, _track.value?.preview)
            putExtra(MusicService.EXTRA_IS_PLAYING, _isPlaying.value)
            putExtra(MusicService.EXTRA_TRACK_NAME, _track.value?.titleShort)
            putExtra(MusicService.EXTRA_ARTIST_NAME, _track.value?.artist?.name)
            putExtra(MusicService.EXTRA_TRACK_IMAGE, _track.value?.album?.coverSmall)
        }
        context.startService(intent)
    }

    @OptIn(UnstableApi::class)
    fun playOrPause(context: Context) {
        val intent = Intent(context, MusicService::class.java).apply {
            action =
                if (_isPlaying.value) MusicService.Action.PAUSE.toString() else MusicService.Action.PLAY.toString()
        }
        context.startService(intent)
        _isPlaying.value = !_isPlaying.value
    }

    private fun stop(context: Context) {
        val intent = Intent(context, MusicService::class.java).apply {
            action = MusicService.Action.STOP.toString()
        }
        context.startService(intent)
    }
}