package com.example.musicapp.data.repository

import com.example.musicapp.data.model.Genre
import com.example.musicapp.data.model.Genres
import kotlinx.coroutines.flow.Flow

interface GenresRepository : ItemRepository<Genre> {
    fun getItemStream(id: Int): Flow<Genre?>
}