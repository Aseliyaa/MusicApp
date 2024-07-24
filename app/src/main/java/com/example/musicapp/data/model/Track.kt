package com.example.musicapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.musicapp.data.model.converter.AlbumConverter
import com.example.musicapp.data.model.converter.ArtistConverter
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tracks")
@TypeConverters(AlbumConverter::class, ArtistConverter::class)
data class Track(
    @PrimaryKey
    @SerializedName("id") var id: String,
    @SerializedName("title") var title: String? = null,
    @SerializedName("title_short") var titleShort: String? = null,
    @SerializedName("title_version") var titleVersion: String? = null,
    @SerializedName("link") var link: String? = null,
    @SerializedName("duration") var duration: Int? = null,
    @SerializedName("rank") var rank: Int? = null,
    @SerializedName("explicit_lyrics") var explicitLyrics: Boolean? = null,
    @SerializedName("explicit_content_lyrics") var explicitContentLyrics: Int? = null,
    @SerializedName("explicit_content_cover") var explicitContentCover: Int? = null,
    @SerializedName("preview") var preview: String? = null,
    @SerializedName("md5_image") var md5Image: String? = null,
    @SerializedName("position") var position: Int? = null,
    @SerializedName("album") var album: Album?,
    @SerializedName("artist") var artist: Artist?,
    @SerializedName("type") var type: String? = null
)

