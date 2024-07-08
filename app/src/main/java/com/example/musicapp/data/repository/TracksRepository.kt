package com.example.musicapp.data.repository

import com.example.musicapp.data.model.Genre
import com.example.musicapp.data.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository: ItemRepository<Track>{
    fun getItemStream(id: String): Flow<Track?>
}