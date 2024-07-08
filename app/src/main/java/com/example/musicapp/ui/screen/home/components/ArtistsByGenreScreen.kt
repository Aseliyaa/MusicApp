package com.example.musicapp.ui.screen.home.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.example.musicapp.ui.screen.home.ErrorScreen
import com.example.musicapp.ui.screen.home.HomeViewModel
import com.example.musicapp.ui.screen.home.LoadingScreen
import com.example.musicapp.ui.viewmodel.AppViewModelProvider

@Composable
fun ArtistsByGenreScreen(
    navController: NavHostController, genreId: String?,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider.Factory)
) {

    LaunchedEffect(key1 = genreId){
        viewModel.getArtistByGenreId(genreId)
    }

    when (val artistUiState = viewModel.artistByGenreIdUiState) {
        is HomeViewModel.UiState.Success ->
            if(artistUiState.data.data != null) {
                ArtistsGrid(
                    artists = artistUiState.data.data,
                    navHostController = navController
                )
            } else {
                ArtistsGrid(
                    artists = emptyList(),
                    navHostController = navController
                )
            }

        is HomeViewModel.UiState.Loading -> LoadingScreen()
        is HomeViewModel.UiState.Error -> ErrorScreen()
    }

}