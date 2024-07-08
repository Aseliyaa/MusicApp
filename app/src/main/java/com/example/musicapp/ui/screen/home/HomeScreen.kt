package com.example.musicapp.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.State
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
import com.example.musicapp.ui.viewmodel.AppViewModelProvider


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




