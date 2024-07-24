package com.example.musicapp.ui.screen.home


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp.ui.viewmodel.AppViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.musicapp.R
import com.example.musicapp.data.model.Album
import com.example.musicapp.data.model.Track
import com.example.musicapp.ui.screen.home.components.TrackItem
import com.example.musicapp.ui.theme.Purple40
import java.util.Locale


@Composable
fun ArtistDetailsScreen(
    navHostController: NavHostController,
    artistId: String,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(key1 = artistId) {
        viewModel.getAlbumsByArtistId(artistId)
        viewModel.getArtistTracks(artistId)
    }

    var tracks = when (val artistTracksUiState = viewModel.artistTracksUiState) {
        is HomeViewModel.UiState.Success -> artistTracksUiState.data.data
        else -> emptyList()
    }
    if (tracks == null) {
        tracks = emptyList()
    }

    LazyColumn() {
        item { LazyColumnText("Albums") }
        item { AlbumsContent(navHostController, viewModel) }
        item { Spacer(modifier = Modifier.size(20.dp)) }
        item { LazyColumnText("Tracks") }
        items(tracks.chunked(2)) { pair ->
            TracksRow(pair)
        }
    }
}

@Composable
fun AlbumsContent(navHostController: NavHostController, viewModel: HomeViewModel) {
    when (val albumsUiState = viewModel.albumsUiState) {
        is HomeViewModel.UiState.Success -> albumsUiState.data.data?.let {
            AlbumsRow(
                navHostController,
                it
            )
        }

        is HomeViewModel.UiState.Loading -> LoadingScreen()
        is HomeViewModel.UiState.Error -> ErrorScreen()
    }
}

@Composable
fun LazyColumnText(text: String) {
    Text(
        text = text,
        color = Color.Black,
        fontSize = 24.sp,
        fontWeight = FontWeight.W800,
        modifier = Modifier.padding(20.dp, 0.dp),
    )
}

@Composable
fun TracksRow(pair: List<Track>) {
    Row(
        modifier = Modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        pair.forEach { track ->
            TrackItem(
                track = track, onItemClick = {},
                modifier = Modifier
                    .weight(1f)
            )
        }
        if (pair.size < 2) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun AlbumsRow(navHostController: NavHostController, albums: List<Album>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(8.dp),
    ) {
        items(albums) { album ->
            GridItem(
                param = album.id,
                uri = album.coverBig,
                name = album.title,
                modifier = Modifier,
                onItemClick = { albumId ->
                    navHostController.navigate("album/$albumId")
                }
            )
        }
    }
}


