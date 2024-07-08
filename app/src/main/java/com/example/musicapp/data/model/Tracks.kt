package com.example.musicapp.data.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName


data class Tracks(
    @SerializedName("data") var data: List<Track>,
    @SerializedName("total") var total: Int? = null
)
