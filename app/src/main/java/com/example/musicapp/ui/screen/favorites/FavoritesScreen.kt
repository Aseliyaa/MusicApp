package com.example.musicapp.ui.screen.favorites


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.musicapp.ui.screen.home.ErrorScreen
import com.example.musicapp.ui.screen.home.HomeViewModel
import com.example.musicapp.ui.screen.home.LoadingScreen
import com.example.musicapp.ui.screen.home.TracksRow
import com.example.musicapp.ui.viewmodel.AppViewModelProvider

@Composable
fun FavoritesScreen(
    navHostController: NavHostController,
    viewModel: FavoritesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider.Factory)
) {
    val favoriteTracks = viewModel.favoriteTracks.collectAsState()

    LaunchedEffect(key1 = Unit, key2 = favoriteTracks.value) {
        viewModel.updatePlaylist()
    }
    when(val favoriteTracksUiState = viewModel.favoriteTracksUiState){
        is HomeViewModel.UiState.Success ->
            favoriteTracksUiState.data.data?.let {
                LazyColumn {
                    items(it.chunked(2)){pair ->
                        TracksRow(pair = pair, navHostController = navHostController)
                    }
                }
            }
        is HomeViewModel.UiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is HomeViewModel.UiState.Error -> ErrorScreen(modifier = Modifier.fillMaxSize())
    }
}
