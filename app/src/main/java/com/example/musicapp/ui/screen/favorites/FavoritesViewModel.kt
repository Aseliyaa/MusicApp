package com.example.musicapp.ui.screen.favorites

import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.example.musicapp.data.model.Favorites
import com.example.musicapp.data.model.Track
import com.example.musicapp.data.model.Tracks
import com.example.musicapp.data.repository.FavoritesRepository
import com.example.musicapp.data.repository.TracksRepository
import com.example.musicapp.ui.screen.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val tracksRepository: TracksRepository
): ViewModel() {

    var favoriteTracksUiState: HomeViewModel.UiState<Tracks> by mutableStateOf(HomeViewModel.UiState.Loading())

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
    fun setFavoriteTracks(){
        viewModelScope.launch {
            _favoriteTracks.value = favoritesRepository.getAllItemsStream().first().toTracksList()
            favoriteTracksUiState =
                HomeViewModel.UiState.Success(Tracks(_favoriteTracks.value))
        }
    }

    @OptIn(UnstableApi::class)
    fun addTrack(track: Track){
        viewModelScope.launch {
            try {
                if (!_favoriteTracks.value?.contains(track)!!) {
                    track.isClicked = true
                    favoritesRepository.insertItem(track.toFavorite())
                    _favoriteTracks.value =
                        favoritesRepository.getAllItemsStream().first().toTracksList()
                    favoriteTracksUiState =
                        HomeViewModel.UiState.Success(Tracks(_favoriteTracks.value))
                }
            } catch (e: Exception){
                favoriteTracksUiState = HomeViewModel.UiState.Error()
            }
        }
    }

    fun deleteTrack(track: Track){
        viewModelScope.launch {
            try {
                track.isClicked = false
                favoritesRepository.deleteItem(track.toFavorite())
                _favoriteTracks.value =
                    favoritesRepository.getAllItemsStream().first().toTracksList()
                favoriteTracksUiState = HomeViewModel.UiState.Success(Tracks(_favoriteTracks.value))
            } catch (e: Exception){
                favoriteTracksUiState = HomeViewModel.UiState.Error()
            }
        }
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


