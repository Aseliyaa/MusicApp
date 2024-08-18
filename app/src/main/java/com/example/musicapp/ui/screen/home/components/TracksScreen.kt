package com.example.musicapp.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.musicapp.data.model.Track
import com.example.musicapp.ui.screen.home.ContentGrid
import com.example.musicapp.ui.screen.home.ErrorScreen
import com.example.musicapp.ui.screen.home.GridItem
import com.example.musicapp.ui.screen.home.HomeViewModel
import com.example.musicapp.ui.screen.home.LoadingScreen

import com.example.musicapp.ui.viewmodel.AppViewModelProvider
import java.util.Locale


@Composable
fun TracksGrid(
    tracks: List<Track>,
    navHostController: NavHostController
) {
    ContentGrid(
        list = tracks,
        navigateTo = { trackId ->
            navHostController.navigate("track/$trackId")
        },
    ) { track, modifier, navigateTo ->
        TrackItem(track = track, onItemClick = navigateTo, modifier = modifier)
    }
}

@Composable
fun TracksScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider.Factory)
) {
    when (val tracksUiState = viewModel.tracksUiState) {
        is HomeViewModel.UiState.Success ->
            tracksUiState.data.data?.let {
                TracksGrid(
                    tracks = it,
                    navHostController = navHostController
                )
            }

        is HomeViewModel.UiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is HomeViewModel.UiState.Error -> ErrorScreen(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun TracksByAlbumIdScreen(
    navHostController: NavHostController,
    albumId: String,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(key1 = albumId) {
        viewModel.getAlbumById(albumId)
        viewModel.getAlbumTracks(albumId)
    }
    val album = when (val albumUiState = viewModel.albumUiState) {
        is HomeViewModel.UiState.Success -> albumUiState.data
        is HomeViewModel.UiState.Loading -> null
        is HomeViewModel.UiState.Error -> null
    }

    when (val albumTracksUiState = viewModel.albumTracksUiState) {
        is HomeViewModel.UiState.Success ->
            albumTracksUiState.data.data?.let {
                it.forEach { track ->
                    track.album = album
                }
                ContentGrid(
                    list = it,
                    navigateTo = { trackId ->
                        navHostController.navigate("track/$trackId")
                    },
                ) { track, modifier, navigateTo ->
                    TrackItem(track = track, onItemClick = navigateTo, modifier = modifier)
                }
            }

        is HomeViewModel.UiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is HomeViewModel.UiState.Error -> ErrorScreen(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun TrackItem(track: Track, onItemClick: (String) -> Unit, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .clickable { onItemClick(track.id) }
            .background(Color.Transparent)
            .wrapContentHeight()
            .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(
                Brush.linearGradient(
                    listOf(Color.White, Color.Black),
                    Offset.Zero,
                    Offset.Infinite,
                    TileMode.Clamp
                )
            )
        )
        {
            AsyncImage(
                model = track.album?.coverBig,
                contentDescription = "picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            )

            track.title?.let {
                val title = if (it.length > 12) it[0].uppercase(Locale.ROOT)
                    .plus(it.substring(1, 10).lowercase(Locale.ROOT)).plus("...")
                else it
                Text(
                    text = title,
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(2f)
                )
            }
        }
    }
}
