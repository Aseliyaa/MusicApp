package com.example.musicapp.data.repository.impl

import com.example.musicapp.data.dao.FavoritesDao
import com.example.musicapp.data.dao.TrackDao
import com.example.musicapp.data.model.Favorites
import com.example.musicapp.data.model.Track
import com.example.musicapp.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class FavoritesRepositoryImpl(private val favoritesDao: FavoritesDao): FavoritesRepository {
    override suspend fun getAllItemsStream(): Flow<List<Favorites>> {
        return favoritesDao.getAllTracks()
    }

    override suspend fun insertItem(item: Favorites) {
        favoritesDao.insert(item)
    }

    override suspend fun deleteItem(item: Favorites) {
        favoritesDao.delete(item)
    }

    override suspend fun updateItem(item: Favorites) {
        favoritesDao.update(item)
    }

    override suspend fun deleteAll() {
        favoritesDao.deleteAll()
    }

    override suspend fun insertAll(items: List<Favorites>) {
        favoritesDao.insertAll(items)
    }

    override suspend fun getItemStream(id: String): Flow<Favorites?> {
        return favoritesDao.getTrack(id)
    }
}