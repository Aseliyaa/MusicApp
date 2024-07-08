package com.example.musicapp.data.repository

import kotlinx.coroutines.flow.Flow

interface ItemRepository<T> {
    fun getAllItemsStream(): Flow<List<T>>

    suspend fun insertItem(item: T)
    suspend fun deleteItem(item: T)
    suspend fun updateItem(item: T)
    suspend fun deleteAll()
    suspend fun insertAll(items: List<T>)
}