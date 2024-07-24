package com.example.musicapp.network.service

import com.example.musicapp.data.model.Album
import com.example.musicapp.data.model.Albums
import com.example.musicapp.data.model.Artist
import com.example.musicapp.data.model.Artists
import com.example.musicapp.data.model.Chart
import com.example.musicapp.data.model.Genres
import com.example.musicapp.data.model.Tracks

import retrofit2.http.GET
import retrofit2.http.Path

interface DeezerApiService {
    @GET("genre")
    suspend fun getGenres(): Genres

    @GET("genre/{genre_id}/artists")
    suspend fun getArtists(@Path("genre_id") genreId: String) : Artists

    @GET("chart/{chart_id}")
    suspend fun getChart(@Path("chart_id") chartId: Int) : Chart

    @GET("artist/{artist_id}/top?limit=50")
    suspend fun getArtistTracks(@Path("artist_id") artistId: String) : Tracks

    @GET("artist/{artist_id}/albums")
    suspend fun getArtistAlbums(@Path("artist_id") artistId: String): Albums

    @GET("album/{album_id}/tracks")
    suspend fun getTracksByAlbumId(@Path("album_id") albumId: String): Tracks

    @GET("album/{album_id}")
    suspend fun getAlbumById(@Path("album_id") albumId: String): Album
}