package com.example.musicapp.data.repository.impl

import com.example.musicapp.data.dao.TrackDao
import com.example.musicapp.data.model.Track
import com.example.musicapp.data.repository.TracksRepository
import kotlinx.coroutines.flow.Flow

class TracksRepositoryImpl(private val trackDao: TrackDao) : TracksRepository {
    override suspend fun getAllItemsStream(): Flow<List<Track>> {
        return trackDao.getAllTracks()
    }

    override suspend fun getItemStream(id: String): Flow<Track?> {
        return trackDao.getTrack(id)
    }

    override suspend fun insertItem(item: Track) {
        trackDao.insert(item)
    }

    override suspend fun deleteItem(item: Track) {
        trackDao.delete(item)
    }

    override suspend fun updateItem(item: Track) {
        trackDao.update(item)
    }

    override suspend fun deleteAll() {
        trackDao.deleteAll()
    }

    override suspend fun insertAll(items: List<Track>) {
        trackDao.insertAll(items)
    }

}