package com.example.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.musicapp.data.model.Favorites
import com.example.musicapp.data.model.Track
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoritesDao: ItemDao<Favorites> {
    @Query("SELECT * FROM favorites WHERE id = :id")
    fun getTrack(id: String): Flow<Favorites>

    @Query("SELECT * FROM favorites") fun getAllTracks(): Flow<List<Favorites>>

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()
}