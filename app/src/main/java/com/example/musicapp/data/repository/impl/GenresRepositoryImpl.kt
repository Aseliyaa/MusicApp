package com.example.musicapp.data.repository.impl

import com.example.musicapp.data.dao.GenreDao
import com.example.musicapp.data.model.Genre
import com.example.musicapp.data.repository.GenresRepository
import kotlinx.coroutines.flow.Flow

class GenresRepositoryImpl(private val genreDao: GenreDao) : GenresRepository {
    override fun getAllItemsStream(): Flow<List<Genre>> {
        return genreDao.getAllGenres()
    }

    override fun getItemStream(id: Int): Flow<Genre?> {
        return genreDao.getGenre(id)
    }

    override suspend fun insertItem(item: Genre) {
        genreDao.insert(item)
    }

    override suspend fun deleteItem(item: Genre) {
        genreDao.delete(item)
    }

    override suspend fun updateItem(item: Genre) {
        genreDao.update(item)
    }

    override suspend fun deleteAll() {
        genreDao.deleteAll()
    }

    override suspend fun insertAll(items: List<Genre>) {
        genreDao.insertAll(items)
    }

}