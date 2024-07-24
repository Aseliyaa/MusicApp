package com.example.musicapp.data.model.converter

import androidx.room.TypeConverter
import com.example.musicapp.data.model.Album
import com.example.musicapp.data.model.Artist
import com.google.gson.Gson

class AlbumConverter {
    @TypeConverter
    fun fromAlbum(album: Album?) : String?{
        return if(album == null) null else Gson().toJson(album)
    }

    @TypeConverter
    fun toAlbum(albumStr: String?) : Album?{
        return if(albumStr == null) null else Gson().fromJson(albumStr, Album::class.java)
    }
}