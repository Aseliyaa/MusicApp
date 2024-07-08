package com.example.musicapp.ui.screen.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.musicapp.ui.screen.home.components.ArtistsGrid
import com.example.musicapp.ui.screen.home.components.CommonScreenComponents
import com.example.musicapp.ui.viewmodel.AppViewModelProvider

@Composable
fun ArtistsByGenreScreen(
    navController: NavHostController, genreId: String?,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider.Factory)
) {

    if (genreId != null) {
        viewModel.getArtistByGenreId(genreId)
    }

    when (val artistUiState = viewModel.artistByGenreIdUiState) {
        is HomeViewModel.UiState.Success ->
            ArtistsGrid(
                artists = artistUiState.data.data,
                navHostController = navController
            )

        is HomeViewModel.UiState.Loading -> LoadingScreen()
        is HomeViewModel.UiState.Error -> ErrorScreen()
    }

}