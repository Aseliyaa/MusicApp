package com.example.musicapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "artist")
data class Artist(
    @PrimaryKey
    @SerializedName("id") var id: String,
    @SerializedName("name") var name: String? = null,
    @SerializedName("picture") var picture: String? = null,
    @SerializedName("picture_small") var pictureSmall: String? = null,
    @SerializedName("picture_medium") var pictureMedium: String? = null,
    @SerializedName("picture_big") var pictureBig: String? = null,
    @SerializedName("picture_xl") var pictureXl: String? = null,
    @SerializedName("radio") var radio: Boolean? = null,
    @SerializedName("tracklist") var tracklist: String? = null,
    @SerializedName("type") var type: String? = null

)