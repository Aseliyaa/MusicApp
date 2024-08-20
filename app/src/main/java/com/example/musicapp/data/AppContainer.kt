package com.example.musicapp.data

import android.content.Context
import com.example.musicapp.data.db.AppDatabase
import com.example.musicapp.data.repository.ArtistsRepository
import com.example.musicapp.data.repository.FavoritesRepository
import com.example.musicapp.data.repository.GenresRepository
import com.example.musicapp.data.repository.TracksRepository
import com.example.musicapp.data.repository.impl.ArtistsRepositoryImpl
import com.example.musicapp.data.repository.impl.FavoritesRepositoryImpl
import com.example.musicapp.data.repository.impl.GenresRepositoryImpl
import com.example.musicapp.data.repository.impl.TracksRepositoryImpl

interface AppContainer {
    val genresRepository: GenresRepository
    val artistsRepository: ArtistsRepository
    val tracksRepository: TracksRepository
    val favoritesRepository: FavoritesRepository
}

class AppDataContainer(private val context: Context) : AppContainer{
    override val genresRepository: GenresRepository by lazy {
        GenresRepositoryImpl(AppDatabase.getDatabase(context).genreDao())
    }
    override val artistsRepository: ArtistsRepository by lazy {
        ArtistsRepositoryImpl(AppDatabase.getDatabase(context).artistDao())
    }
    override val tracksRepository: TracksRepository by lazy {
        TracksRepositoryImpl(AppDatabase.getDatabase(context).trackDao())
    }
    override val favoritesRepository: FavoritesRepository by lazy {
        FavoritesRepositoryImpl(AppDatabase.getDatabase(context).favoritesDao())
    }
}