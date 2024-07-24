package com.example.musicapp.data.repository

import com.example.musicapp.data.model.Artist
import com.example.musicapp.data.model.Genre
import kotlinx.coroutines.flow.Flow

interface ArtistsRepository : ItemRepository<Artist> {
}