package com.example.musicapp.data.model.converter

import androidx.room.TypeConverter
import com.example.musicapp.data.model.Artist
import com.google.gson.Gson

class ArtistConverter {
    @TypeConverter
    fun fromArtist(artist: Artist?) : String?{
        return if(artist == null) null else Gson().toJson(artist)
    }

    @TypeConverter
    fun toArtist(artistStr: String?) : Artist?{
        return if(artistStr == null) null else Gson().fromJson(artistStr, Artist::class.java)
    }
}