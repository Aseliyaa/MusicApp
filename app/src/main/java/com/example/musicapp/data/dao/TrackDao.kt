package com.example.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.musicapp.data.model.Artist
import com.example.musicapp.data.model.Track
import com.example.musicapp.data.model.Tracks
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao : ItemDao<Track> {
    @Query("SELECT * FROM tracks WHERE id = :id")
    fun getTrack(id: String): Flow<Track>

    @Query("SELECT * FROM tracks")
    fun getAllTracks(): Flow<List<Track>>

    @Query("DELETE FROM tracks")
    suspend fun deleteAll()
}