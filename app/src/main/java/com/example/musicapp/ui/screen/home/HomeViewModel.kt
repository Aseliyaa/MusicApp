package com.example.musicapp.ui.screen.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.model.Artists
import com.example.musicapp.data.model.Genres
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
        private set

    var artistUiState: UiState<Artists> by mutableStateOf(UiState.Loading())
        private set

    var tracksUiState: UiState<Tracks> by mutableStateOf(UiState.Loading())
        private set

    var artistByGenreIdUiState: UiState<Artists> by mutableStateOf(UiState.Loading())
        private set

    fun selectItem(index: Int) {
        _selectedItemIndex.value = index
    }

    init {
        getGenres()
        getArtists()
        getTracks()
    }

    private fun getTracks() {
        viewModelScope.launch {
            val tracksFromDbDeferred = async { tracksRepository.getAllItemsStream().first() }
            val tracksFromDb = tracksFromDbDeferred.await()

            if (tracksFromDb.isNotEmpty()) {
                tracksUiState = UiState.Success(Tracks(tracksFromDb))
            }

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
                    Log.d("TAG", tracksFromApi.toString())
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

            try {
                val artistsFromApi = genresFromDb.map { genre ->
                    async {
                        try {
                            DeezerApi.retrofitService.getArtists(genre.id).data
                        } catch (e: IOException) {
                            emptyList()
                        }
                    }
                }.awaitAll().flatten().toSet()

                artistUiState = if (artistsFromApi.isNotEmpty()) {
                    artistsRepository.deleteAll()
                    artistsRepository.insertAll(artistsFromApi.toList())

                    UiState.Success(Artists(artistsRepository.getAllItemsStream().first()))
                } else {
                    UiState.Error()
                }

            } catch (e: Exception) {
                artistUiState = UiState.Error()
            }
        }
    }

    fun getArtistByGenreId(genreIdStr: String?) {
        viewModelScope.launch {
            val genreId = genreIdStr?.toInt()
            if (genreId != null) {
                artistByGenreIdUiState = try {
                    val artistsByGenreId = DeezerApi.retrofitService.getArtists(genreId)
                    UiState.Success(artistsByGenreId)
                } catch (e: IOException) {
                    UiState.Error()
                }
            }
        }
    }
//    fun getArtistByGenreId(genreIdStr: String?) {
//        viewModelScope.launch {
//            val genreId = genreIdStr?.toInt()
//            try {
//                artistByGenreIdUiState = if (genreId != null) {
//                    val artistsByGenreIdDeferred = async {
//                        try {
//                            DeezerApi.retrofitService.getArtists(genreId).data
//                        } catch (e: IOException) {
//                            emptyList()
//                        }
//                    }
//                    val artistsByGenreId = artistsByGenreIdDeferred.await()
//                    UiState.Success(Artists(artistsByGenreId))
//
//                } else {
//                    UiState.Error()
//                }
//            } catch (e: Exception) {
//                artistByGenreIdUiState = UiState.Error()
//            }
//        }
//    }
}


