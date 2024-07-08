package com.example.musicapp.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.musicapp.R
import com.example.musicapp.ui.screen.home.components.ArtistsGrid
import com.example.musicapp.ui.screen.home.components.CommonScreenComponents
import com.example.musicapp.ui.screen.home.components.GenresGrid
import com.example.musicapp.ui.screen.home.components.TracksGrid
import com.example.musicapp.ui.viewmodel.AppViewModelProvider


@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val selectedItemIndex by viewModel.selectedItemIndex.collectAsState()

    CommonScreenComponents(
        navHostController = navHostController,
        content = {
            SelectedItemScreen(navHostController, viewModel, selectedItemIndex, Modifier)
        }
    )
//    Column(
//        modifier = Modifier
//            .padding(20.dp)
//
//    ) {
//        ProfileAndSearchRow()
//        Spacer(modifier = Modifier.height(12.dp))
//        HomeMenuRow(
//            selectedItemIndex = selectedItemIndex,
//            onItemSelected = { viewModel.selectItem(it) }
//        )
//        Spacer(modifier = Modifier.height(20.dp))
//        HeaderRow()
//        SelectedItemScreen(navHostController, viewModel, selectedItemIndex, Modifier)
//    }
}

@Composable
fun SelectedItemScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel,
    selectedItemIndex: Int,
    modifier: Modifier
) {
    val genresUiState = viewModel.genresUiState
    val artistUiState = viewModel.artistUiState
    val tracksUiState = viewModel.tracksUiState

    when (selectedItemIndex) {
        0 -> {
            HandleUiState(
                uiState = genresUiState,
                onSuccess = { genres ->
                    GenresGrid(
                        genres.data,
                        navHostController
                    )
                },
                modifier = modifier
            )
        }

        1 -> {
            HandleUiState(
                uiState = artistUiState,
                onSuccess = { artists ->
                    ArtistsGrid(
                        artists.data,
                        navHostController
                    )
                },
                modifier = modifier
            )
        }

        2 -> {
            HandleUiState(
                uiState = tracksUiState,
                onSuccess = { artists ->
                    TracksGrid(
                        artists.data,
                        navHostController
                    )
                },
                modifier = modifier
            )
        }
    }
}

@Composable
fun <T> HandleUiState(
    uiState: HomeViewModel.UiState<T>,
    onSuccess: @Composable (data: T) -> Unit,
    modifier: Modifier
) {
    when (uiState) {
        is HomeViewModel.UiState.Success -> onSuccess(uiState.data)
        is HomeViewModel.UiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is HomeViewModel.UiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.profile),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Image(
//            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
//        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun HeaderRow() {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {

        Text(
            text = "Find the best\nmusic for you",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.width(20.dp))

        Image(
            painter = painterResource(id = R.drawable.arm),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .height(60.dp)
                .offset(x = 20.dp)
        )
    }
}

@Composable
fun HomeMenuRow(selectedItemIndex: Int, onItemSelected: (Int) -> Unit) {
    val commonListItems = listOf("Genres", "Artists", "Songs", "Favorites")
    val color = colorResource(id = R.color.word_grey)

    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = color,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        items(commonListItems.size) { index ->
            TextItem(
                item = commonListItems[index],
                isSelected = (selectedItemIndex == index),
                onClick = { onItemSelected(index) }
            )
        }
    }
}

@Composable
fun ProfileAndSearchRow() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()

    ) {
        ProfileBox()
        SearchBox()
    }
}

@Composable
fun SearchBox() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp)
            .background(
                color = colorResource(id = R.color.light_grey),
                shape = CircleShape
            ),
    ) {
        Image(
            imageVector = Icons.Default.Search,
            contentDescription = "search",
            modifier = Modifier.size(35.dp)
        )
    }
}

@Composable
fun ProfileBox() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp)
            .background(
                color = colorResource(id = R.color.yellow),
                shape = CircleShape
            ),
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "profile",
            modifier = Modifier.size(35.dp)
        )
    }
}

@Composable
fun TextItem(item: String, isSelected: Boolean, onClick: () -> Unit) {
    val color =
        if (isSelected) colorResource(R.color.purple) else colorResource(id = R.color.word_grey)

    Text(
        text = item,
        fontSize = 20.sp,
        fontWeight = FontWeight.W300,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .clickable(
                onClick = onClick
            )
            .drawBehind {
                val strokeWidth = if (isSelected) 3.dp.toPx() else 2.dp.toPx()
                val y = size.height + 10.dp.toPx() - strokeWidth / 2
                drawLine(
                    color = color,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        color = color,
    )
}


//@RequiresApi(Q)
//@Preview
//@Composable
//fun HomeScreenPreview() {
//    val e1 = Genre(
//        0,
//        "Все",
//        "https://api.deezer.com/genre/0/image",
//        "https://e-cdns-images.dzcdn.net/images/misc//56x56-000000-80-0-0.jpg",
//        "https://e-cdns-images.dzcdn.net/images/misc//250x250-000000-80-0-0.jpg",
//        "https://e-cdns-images.dzcdn.net/images/misc//500x500-000000-80-0-0.jpg",
//        "https://e-cdns-images.dzcdn.net/images/misc//1000x1000-000000-80-0-0.jpg",
//        "genre"
//    )
//    val list: List<Genre> = listOf(e1, e1, e1, e1, e1, e1)
//    HomeScreen(
//        HomeViewModel(),
//        genresUiState = HomeViewModel.UiState.Success(Genres(list)),
//        homeViewModel.artistUiState
//    )
//}

