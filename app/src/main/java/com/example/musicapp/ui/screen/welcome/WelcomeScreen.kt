package com.example.musicapp.ui.screen.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.musicapp.ui.viewmodel.AppViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@Composable
fun WelcomeScreen(
    navHostController: NavHostController,
    viewModel: WelcomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val pageCount = 3
    LocalContext.current
    val pagerState = rememberPagerState(pageCount = {
        pageCount
    })
    val pagerPageTextList = listOf(
        "Listen with \n Joy",
        "Dive into \n Sound",
        "Discover New \n Tracks"
    )
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.bg_grey))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(7f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.welcome),
                        contentDescription = "background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(6f)
                        .background(Color.Transparent),
                    shape = RoundedCornerShape(
                        topStart = 40.dp,
                        topEnd = 40.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                ) {
                    WelcomePager(
                        pagerState = pagerState,
                        pagerPageTextList = pagerPageTextList,
                        pageCount = pageCount,
                        scope = scope,
                        viewModel = viewModel,
                        navHostController
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomePager(
    pagerState: PagerState,
    pagerPageTextList: List<String>,
    pageCount: Int,
    scope: CoroutineScope,
    viewModel: WelcomeViewModel,
    navHostController: NavHostController
) {
    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = pagerPageTextList[pagerState.currentPage],
                color = Color.Black,
                fontSize = 48.sp,
                fontWeight = FontWeight(600)
            )

            Spacer(modifier = Modifier.height(50.dp))

            LineIndicator(Modifier, pagerState, pageCount = pageCount)

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    scope.launch {
                        viewModel._currentPage.value = pagerState.currentPage
                        viewModel.scrollNext(pagerState, pageCount, navHostController)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(
                        id = R.color.purple
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    text =
                    if (pagerState.currentPage == pageCount - 1) "Get Started"
                    else "Next",
                )
            }
        }
    }
}

@Composable
fun LineIndicator(
    modifier: Modifier = Modifier,
    state: PagerState,
    pageCount: Int
) {
    Row(
        modifier = modifier
            .width(56.dp * pageCount)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        for (i in 0 until pageCount) {
            val offset = state.indicatorOffsetForPage(i)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .weight(.5f + (offset * 2f))
                    .height(16.dp)
                    .border(
                        width = 2.dp,
                        color = colorResource(id = R.color.purple),
                        shape = CircleShape
                    )
                    .background(
                        color =
                        if (i == state.currentPage)
                            colorResource(id = R.color.purple)
                        else
                            Color.LightGray,
                        shape = CircleShape
                    ),
            ) {

            }
        }
    }
}

fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

fun PagerState.indicatorOffsetForPage(page: Int) =
    1f - offsetForPage(page).coerceIn(-1f, 1f).absoluteValue

//@Preview
//@Composable
//fun WelcomeScreenPreview() {
//    WelcomeScreen(WelcomeViewModel(), NavHostController(LocalContext.current))
//}