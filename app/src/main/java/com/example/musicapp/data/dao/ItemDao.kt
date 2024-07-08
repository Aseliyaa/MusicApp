package com.example.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface ItemDao<T> {
    @Insert
    suspend fun insert(el: T)

    @Update
    suspend fun update(el: T)

    @Delete
    suspend fun delete(el: T)

    @Insert
    suspend fun insertAll(list: List<T>)
}