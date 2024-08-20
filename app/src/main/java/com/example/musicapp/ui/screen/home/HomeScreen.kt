package com.example.musicapp.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.musicapp.R
import com.example.musicapp.ui.viewmodel.AppViewModelProvider

@Composable
fun <T> ContentGrid(
    list: List<T>,
    navigateTo: (String) -> Unit,
    itemContent: @Composable (T, Modifier, (String) -> Unit) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(list.size) { index ->
            itemContent(list[index], Modifier, navigateTo)
        }
    }
}

@Composable
fun GridItem(
    param: String,
    uri: String?,
    name: String?,
    modifier: Modifier,
    onItemClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(40.dp),
        modifier = modifier
            .clickable(onClick = { onItemClick(param) })
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = uri,
                contentDescription = "picture",
                contentScale = ContentScale.Crop
            )

            name?.let {
                val title = if (it.length > 21) it.substring(0, 18) + "..." else it
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(PaddingValues(10.dp, 25.dp))

                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W500,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
        ) {
        Image(
            painter = painterResource(R.drawable.loading),
            contentDescription = stringResource(R.string.loading)
        )
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = modifier.size(80.dp),
            painter = painterResource(id = R.drawable.error),
            contentDescription = stringResource(R.string.loading_failed)
        )
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
fun HomeMenuRow(
    selectedItemIndex: State<Int>,
    navHostController: NavHostController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

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
                selectedItemIndex.value == index,
                item = commonListItems[index], index
            ) { itemIndex ->
                viewModel.selectItem(itemIndex)
                navHostController.navigate("home/$itemIndex")
            }
        }
    }
}

@Composable
fun TextItem(isSelected: Boolean, item: String, itemIndex: Int, onItemClick: (Int) -> Unit) {
    val color =
        if (isSelected) colorResource(R.color.purple) else colorResource(id = R.color.word_grey)

    Text(
        text = item,
        fontSize = 20.sp,
        fontWeight = FontWeight.W300,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .clickable(
                onClick = {
                    onItemClick(itemIndex)
                }
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


