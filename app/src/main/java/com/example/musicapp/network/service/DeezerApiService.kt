package com.example.musicapp.network.service

import com.example.musicapp.data.model.Artist
import com.example.musicapp.data.model.Artists
import com.example.musicapp.data.model.Chart
import com.example.musicapp.data.model.Genres

import retrofit2.http.GET
import retrofit2.http.Path

interface DeezerApiService {
    @GET("genre")
    suspend fun getGenres(): Genres

    @GET("genre/{genre_id}/artists")
    suspend fun getArtists(@Path("genre_id") genreId: Int) : Artists

    @GET("chart/{chart_id}")
    suspend fun getChart(@Path("chart_id") chartId: Int) : Chart

}