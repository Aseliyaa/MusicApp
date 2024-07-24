package com.example.musicapp.data.repository.impl

import com.example.musicapp.data.dao.ArtistDao
import com.example.musicapp.data.model.Artist
import com.example.musicapp.data.repository.ArtistsRepository
import kotlinx.coroutines.flow.Flow

class ArtistsRepositoryImpl(private val artistDao: ArtistDao): ArtistsRepository {
    override fun getAllItemsStream(): Flow<List<Artist>> {
        return artistDao.getAllArtists()
    }

    override fun getItemStream(id: String): Flow<Artist?> {
        return artistDao.getArtist(id)
    }

    override suspend fun insertItem(item: Artist) {
        artistDao.insert(item)
    }

    override suspend fun deleteItem(item: Artist) {
        artistDao.delete(item)
    }

    override suspend fun updateItem(item: Artist) {
        artistDao.update(item)
    }

    override suspend fun deleteAll() {
        artistDao.deleteAll()
    }

    override suspend fun insertAll(items: List<Artist>) {
        artistDao.insertAll(items)
    }

}