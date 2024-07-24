package com.example.musicapp.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.musicapp.data.model.Artist
import com.example.musicapp.ui.screen.home.ContentGrid
import com.example.musicapp.ui.screen.home.ErrorScreen
import com.example.musicapp.ui.screen.home.GridItem
import com.example.musicapp.ui.screen.home.HomeViewModel
import com.example.musicapp.ui.screen.home.LoadingScreen
import com.example.musicapp.ui.viewmodel.AppViewModelProvider


@Composable
fun ArtistsGrid(
    artists: List<Artist>,
    navHostController: NavHostController
) {
    ContentGrid(
        list = artists,
        navigateTo = { artistId ->
            navHostController.navigate("artist/$artistId")
        },
    ) { artist, modifier, navigateTo ->
        GridItem(
            param = artist.id,
            uri = artist.pictureBig,
            name = artist.name,
            modifier = modifier,
            onItemClick = navigateTo
        )
    }
}


@Composable
fun ArtistsScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider.Factory)
){
    when (val artistsUiState = viewModel.artistUiState) {
        is HomeViewModel.UiState.Success ->
            artistsUiState.data.data?.let { ArtistsGrid(
                artists = it,
                navHostController = navHostController
            ) }
        is HomeViewModel.UiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is HomeViewModel.UiState.Error -> ErrorScreen(modifier = Modifier.fillMaxSize())
    }
}


