package com.example.musicapp.ui.screen.home.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.musicapp.ui.screen.home.ErrorScreen
import com.example.musicapp.ui.screen.home.HomeViewModel
import com.example.musicapp.ui.screen.home.LoadingScreen
import com.example.musicapp.ui.viewmodel.AppViewModelProvider

@Composable
fun ArtistsByGenreScreen(
    navController: NavHostController,
    genreId: String,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider.Factory)
) {

    LaunchedEffect(key1 = genreId) {
        viewModel.getArtistByGenreId(genreId)
    }

    when (val artistUiState = viewModel.artistByGenreIdUiState) {
        is HomeViewModel.UiState.Success ->
            artistUiState.data.data?.let {
                ArtistsGrid(artists = it, navHostController = navController)
            }
        is HomeViewModel.UiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is HomeViewModel.UiState.Error -> ErrorScreen(modifier = Modifier.fillMaxSize())
    }
}