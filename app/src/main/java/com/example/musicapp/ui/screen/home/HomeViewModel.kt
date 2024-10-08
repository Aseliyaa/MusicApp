package com.example.musicapp.ui.screen.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.model.Album
import com.example.musicapp.data.model.Albums
import com.example.musicapp.data.model.Artist
import com.example.musicapp.data.model.Artists
import com.example.musicapp.data.model.Genres
import com.example.musicapp.data.model.Track
import com.example.musicapp.data.model.Tracks
import com.example.musicapp.data.repository.ArtistsRepository
import com.example.musicapp.data.repository.GenresRepository
import com.example.musicapp.data.repository.TracksRepository
import com.example.musicapp.network.api.DeezerApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException


class HomeViewModel(
    private val genresRepository: GenresRepository,
    private val artistsRepository: ArtistsRepository,
    private val tracksRepository: TracksRepository
) : ViewModel() {
    private val _selectedItemIndex = MutableStateFlow(0)
    val selectedItemIndex: StateFlow<Int> = _selectedItemIndex

    sealed interface UiState<T> {
        data class Success<T>(val data: T) : UiState<T>
        class Error<T> : UiState<T>
        class Loading<T> : UiState<T>
    }

    var genresUiState: UiState<Genres> by mutableStateOf(UiState.Loading())

    var artistUiState: UiState<Artists> by mutableStateOf(UiState.Loading())

    var tracksUiState: UiState<Tracks> by mutableStateOf(UiState.Loading())

    var artistByGenreIdUiState: UiState<Artists> by mutableStateOf(UiState.Loading())

    var artistTracksUiState: UiState<Tracks> by mutableStateOf(UiState.Loading())

    var albumsUiState: UiState<Albums> by mutableStateOf(UiState.Loading())

    var albumTracksUiState: UiState<Tracks> by mutableStateOf(UiState.Loading())
    var albumUiState: UiState<Album> by mutableStateOf(UiState.Loading())


    fun selectItem(index: Int) {
        _selectedItemIndex.value = index
    }

    init {
        getGenres()
        getArtists()
    }

    fun getTracks() {
        viewModelScope.launch {
            tracksRepository.deleteAll()
            try {
                val tracksFromApiDeferred = async {
                    try {
                        DeezerApi.retrofitService.getChart(0).tracks?.data
                    } catch (e: IOException) {
                        null
                    }
                }
                val tracksFromApi = tracksFromApiDeferred.await()

                if (tracksFromApi != null) {
                    tracksRepository.deleteAll()
                    tracksRepository.insertAll(tracksFromApi)
                    tracksUiState = UiState.Success(Tracks(tracksFromApi))
                }
            } catch (e: IOException) {
                tracksUiState = UiState.Error()
            }
        }
    }

    private fun getGenres() {
        viewModelScope.launch {
            val genresFromDbDeferred = async { genresRepository.getAllItemsStream().first() }
            val genresFromDb = genresFromDbDeferred.await()

            if (genresFromDb.isNotEmpty()) {
                genresUiState = UiState.Success(Genres(genresFromDb))
            }

            try {
                val genresFromApiDeferred = async {
                    try {
                        DeezerApi.retrofitService.getGenres().data
                    } catch (e: IOException) {
                        null
                    }
                }
                val genresFromApi = genresFromApiDeferred.await()

                if (genresFromApi != null) {
                    genresRepository.deleteAll()
                    genresRepository.insertAll(genresFromApi)
                    genresUiState = UiState.Success(Genres(genresFromApi))
                }
            } catch (e: IOException) {
                genresUiState = UiState.Error()
            }
        }
    }

    private fun getArtists() {
        viewModelScope.launch {
            val genresFromDb = genresRepository.getAllItemsStream().first()
            val artistsFromDbDeferred = async { artistsRepository.getAllItemsStream().first() }
            val artistsFromDb = artistsFromDbDeferred.await()
            if (artistsFromDb.isNotEmpty()) {
                artistUiState = UiState.Success(Artists(artistsFromDb))
            }
            val artistsFromApi = mutableSetOf<Artist>()
            try {
                val deferredResults = genresFromDb.map { genre ->
                    async {
                        try {
                            val artists =
                                DeezerApi.retrofitService.getArtists(genre.id).data?.toSet()
                                    ?: emptySet()
                            artistsFromApi.addAll(artists)
                        } catch (e: IOException) {
                            emptyList<Artist>()
                        }
                    }
                }

                deferredResults.awaitAll()

                artistUiState = if (artistsFromApi.isNotEmpty()) {
                    artistsRepository.deleteAll()
                    artistsRepository.insertAll(artistsFromApi.toSet().toList())

                    UiState.Success(Artists(artistsRepository.getAllItemsStream().first()))
                } else {
                    UiState.Error()
                }

            } catch (e: Exception) {
                artistUiState = UiState.Error()
            }
        }
    }

    fun getArtistByGenreId(genreId: String) {
        viewModelScope.launch {
            artistByGenreIdUiState = try {
                val artistsByGenreId = DeezerApi.retrofitService.getArtists(genreId)
                UiState.Success(artistsByGenreId)
            } catch (e: IOException) {
                UiState.Error()
            }
        }
    }

    fun getArtistTracks(artistId: String) {
        viewModelScope.launch {
            try {
                val tracksFromApiDeferred = async {
                    try {
                        DeezerApi.retrofitService.getArtistTracks(artistId).data
                    } catch (e: IOException) {
                        null
                    }
                }
                val tracksFromApi = tracksFromApiDeferred.await()
                if (tracksFromApi != null) {
                    tracksRepository.deleteAll()
                    tracksRepository.insertAll(tracksFromApi)
                    artistTracksUiState = UiState.Success(Tracks(tracksFromApi))
                }
            } catch (e: IOException) {
                artistTracksUiState = UiState.Error()
            }
        }
    }

    fun getAlbumsByArtistId(artistId: String) {
        viewModelScope.launch {
            albumsUiState = try {
                val albumsDeferred =
                    async {
                        DeezerApi.retrofitService.getArtistAlbums(artistId)
                    }

                val albums = albumsDeferred.await()
                UiState.Success(albums)
            } catch (e: IOException) {
                UiState.Error()
            }
        }
    }

    fun getAlbumTracks(albumId: String) {
        viewModelScope.launch {
            albumTracksUiState = try {
                val tracksDeferred = async {
                    DeezerApi.retrofitService.getTracksByAlbumId(albumId)
                }
                val albumDeferred = async {
                    DeezerApi.retrofitService.getAlbumById(albumId)
                }
                val tracks = tracksDeferred.await()
                val album = albumDeferred.await()
                tracks.data?.forEach {
                    it.album = album
                }
                tracks.data?.let {
                    tracksRepository.deleteAll()
                    tracksRepository.insertAll(it)
                }
                UiState.Success(tracks)
            } catch (e: IOException) {
                UiState.Error()
            }
        }
    }

    fun getAlbumById(albumId: String) {
        viewModelScope.launch {
            albumUiState = try {
                val albumDeferred = async {
                    DeezerApi.retrofitService.getAlbumById(albumId)
                }
                val album = albumDeferred.await()
                UiState.Success(album)
            } catch (e: IOException) {
                UiState.Error()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}


