package com.example.musicapp.ui.screen.favorites

import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.example.musicapp.data.model.Favorites
import com.example.musicapp.data.model.Track
import com.example.musicapp.data.repository.FavoritesRepository
import com.example.musicapp.data.repository.TracksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val tracksRepository: TracksRepository
): ViewModel() {

    private var _favoriteTracks = MutableStateFlow<List<Track>?>(null)
    var favoriteTracks: StateFlow<List<Track>?> = _favoriteTracks

    private fun List<Favorites>.toTracksList(): List<Track> {
        return this.map { favorite ->
            favorite.toTrack()
        }
    }
    init {
        setFavoriteTracks()
    }

    fun updatePlaylist(){
        viewModelScope.launch {
            _favoriteTracks.value?.let {
                tracksRepository.deleteAll()
                tracksRepository.insertAll(it)
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun setFavoriteTracks(){
        viewModelScope.launch {
            _favoriteTracks.value = favoritesRepository.getAllItemsStream().first().toTracksList()
        }
    }

    @OptIn(UnstableApi::class)
    fun addTrack(track: Track){
        viewModelScope.launch {
            if(!_favoriteTracks.value?.contains(track)!!) {
                track.isClicked = true
                favoritesRepository.insertItem(track.toFavorite())
                _favoriteTracks.value =
                    favoritesRepository.getAllItemsStream().first().toTracksList()
            }
        }
        updatePlaylist()
    }

    fun deleteTrack(track: Track){
        viewModelScope.launch {
            track.isClicked = false
            favoritesRepository.deleteItem(track.toFavorite())
            _favoriteTracks.value = favoritesRepository.getAllItemsStream().first().toTracksList()
        }
        updatePlaylist()
    }

    private fun Favorites.toTrack(): Track{
        return Track(
            id = this.id,
            title = this.title,
            titleShort = this.titleShort,
            titleVersion = this.titleVersion,
            link = this.link,
            duration = this.duration,
            rank = this.rank,
            explicitLyrics = this.explicitLyrics,
            explicitContentLyrics = this.explicitContentLyrics,
            explicitContentCover = this.explicitContentCover,
            preview = this.preview,
            md5Image = this.md5Image,
            position = this.position,
            album = this.album,
            artist = this.artist,
            type = this.type,
            isClicked = this.isClicked
        )
    }
    private fun Track.toFavorite() : Favorites{
        return Favorites(
            id = this.id,
            title = this.title,
            titleShort = this.titleShort,
            titleVersion = this.titleVersion,
            link = this.link,
            duration = this.duration,
            rank = this.rank,
            explicitLyrics = this.explicitLyrics,
            explicitContentLyrics = this.explicitContentLyrics,
            explicitContentCover = this.explicitContentCover,
            preview = this.preview,
            md5Image = this.md5Image,
            position = this.position,
            album = this.album,
            artist = this.artist,
            type = this.type,
            isClicked = this.isClicked
        )
    }
}


