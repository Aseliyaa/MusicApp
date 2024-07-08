package com.example.musicapp.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import com.example.musicapp.ui.screen.home.HomeViewModel
import com.example.musicapp.ui.viewmodel.AppViewModelProvider

@Composable
fun GenresGrid(
    genres: List<Genre>,
    navHostController: NavHostController,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider.Factory)
) {


    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(genres.size) { genreIndex ->
            GenreItem(genres[genreIndex], Modifier,  onItemClick = { selectedCategory ->
                navHostController.navigate("artists/$selectedCategory")
            })
        }
    }
}


@Composable
fun GenreItem(
    genre: Genre,
    modifier: Modifier,
    onItemClick: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(
            topStart = 40.dp,
            topEnd = 40.dp,
            bottomStart = 40.dp,
            bottomEnd = 40.dp,
        ),
        modifier = modifier
            .clickable(onClick = {onItemClick(genre.id)})
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            AsyncImage(
                model = genre.pictureBig,
                contentDescription = "Genre picture",
                contentScale = ContentScale.Crop
            )

            genre.name?.let {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(PaddingValues(0.dp, 25.dp))

                ) {
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W500,
                        color = Color.White
                    )
                }
            }
        }
    }
}

