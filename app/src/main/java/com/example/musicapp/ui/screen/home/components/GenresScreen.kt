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
import com.example.musicapp.data.model.Genre
import com.example.musicapp.ui.screen.home.ContentGrid
import com.example.musicapp.ui.screen.home.ErrorScreen
import com.example.musicapp.ui.screen.home.GridItem
import com.example.musicapp.ui.screen.home.HomeViewModel
import com.example.musicapp.ui.screen.home.LoadingScreen
import com.example.musicapp.ui.viewmodel.AppViewModelProvider

@Composable
fun GenresGrid(
    genres: List<Genre>,
    navHostController: NavHostController
) {
    ContentGrid(
        list = genres,
        navigateTo = { selectedCategory ->
            navHostController.navigate("artists/$selectedCategory")
        },
    ) { genre, modifier, navigateTo ->
        GridItem(
            param = genre.id,
            uri = genre.pictureBig,
            name = genre.name,
            modifier = modifier,
            onItemClick = navigateTo
        )
    }
}

@Composable
fun GenresScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider.Factory)
) {
    when (val genresUiState = viewModel.genresUiState) {
        is HomeViewModel.UiState.Success ->
            genresUiState.data.data?.let {
                GenresGrid(
                    genres = it,
                    navHostController = navHostController
                )
            }

        is HomeViewModel.UiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is HomeViewModel.UiState.Error -> ErrorScreen(modifier = Modifier.fillMaxSize())
    }
}



