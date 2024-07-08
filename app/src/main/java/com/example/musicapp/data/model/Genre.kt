package com.example.musicapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "genre")
data class Genre(
    @PrimaryKey
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String? = null,
    @SerializedName("picture") var picture: String? = null,
    @SerializedName("picture_small") var pictureSmall: String? = null,
    @SerializedName("picture_medium") var pictureMedium: String? = null,
    @SerializedName("picture_big") var pictureBig: String? = null,
    @SerializedName("picture_xl") var pictureXl: String? = null,
    @SerializedName("type") var type: String? = null
)