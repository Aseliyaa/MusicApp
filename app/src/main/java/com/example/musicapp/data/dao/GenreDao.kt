package com.example.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Query

import com.example.musicapp.data.model.Genre
import com.example.musicapp.data.model.Genres
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao: ItemDao<Genre>{
    @Query("SELECT * FROM genre WHERE id = :id")
    fun getGenre(id: Int): Flow<Genre>

    @Query("SELECT * FROM genre")
    fun getAllGenres(): Flow<List<Genre>>

    @Query("DELETE FROM genre")
    suspend fun deleteAll()
}