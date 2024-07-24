package com.example.musicapp.data.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName


data class Genres(
    @SerializedName("data")
    val data: List<Genre>?
)
