package com.example.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.musicapp.data.model.Artist
import com.example.musicapp.data.model.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao: ItemDao<Artist> {
    @Query("SELECT * FROM artist WHERE id = :id")
    fun getArtist(id: Int): Flow<Artist>

    @Query("SELECT * FROM artist")
    fun getAllArtists(): Flow<List<Artist>>

    @Query("DELETE FROM artist")
    suspend fun deleteAll()
}