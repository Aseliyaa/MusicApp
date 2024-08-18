package com.example.musicapp.ui.screen.home


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp.ui.viewmodel.AppViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.musicapp.data.model.Album
import com.example.musicapp.data.model.Track
import com.example.musicapp.ui.screen.home.components.TrackItem


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
            TracksRow(pair, navHostController)
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
fun TracksRow(pair: List<Track>, navHostController: NavHostController) {
    Row(
        modifier = Modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        pair.forEach { track ->
            TrackItem(
                track = track, onItemClick = { trackId ->
                    navHostController.navigate("track/$trackId")
                },
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


