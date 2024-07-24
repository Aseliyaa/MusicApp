package com.example.musicapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicapp.data.dao.ArtistDao
import com.example.musicapp.data.dao.GenreDao
import com.example.musicapp.data.dao.TrackDao
import com.example.musicapp.data.model.Artist
import com.example.musicapp.data.model.Genre
import com.example.musicapp.data.model.Track
import com.example.musicapp.data.model.converter.AlbumConverter
import com.example.musicapp.data.model.converter.ArtistConverter

@Database(entities = [Genre::class, Artist::class, Track::class], version = 9, exportSchema = false)
@TypeConverters(AlbumConverter::class, ArtistConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun genreDao(): GenreDao
    abstract fun artistDao(): ArtistDao
    abstract fun trackDao(): TrackDao

    companion object{
        @Volatile
        private var Instance : AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase {
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, AppDatabase::class.java, "genre_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}