package com.example.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class Chart(
    @SerializedName("tracks") var tracks: Tracks?,
)
