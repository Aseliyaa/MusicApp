package com.example.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class Albums(
    @SerializedName("data") var data: List<Album>?,
    @SerializedName("total") var total: Int? = null
)
