package com.example.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class Artists(
    @SerializedName("data")
    var data: List<Artist>?
)