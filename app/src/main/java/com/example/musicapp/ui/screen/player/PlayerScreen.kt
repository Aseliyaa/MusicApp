package com.example.musicapp.ui.screen.player

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.musicapp.R
import com.example.musicapp.data.model.Favorites
import com.example.musicapp.data.model.Track
import com.example.musicapp.ui.screen.favorites.FavoritesViewModel
import com.example.musicapp.ui.viewmodel.AppViewModelProvider
import kotlinx.coroutines.delay


@RequiresApi(value = 33)
@Composable
fun PlayerScreen(
    navHostController: NavHostController,
    trackId: String,
    playerViewModel: PlayerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val mediaPlayer = playerViewModel.mediaPlayer.collectAsState().value

    val playlist = playerViewModel.playlist.collectAsState()
    val track = playerViewModel.track.collectAsState()
    val isPlaying = playerViewModel.isPlaying.collectAsState()

    val currentPosition = remember { mutableLongStateOf(0) }
    val sliderPosition = remember { mutableLongStateOf(0) }
    val totalDuration = remember { mutableLongStateOf(0) }

    LaunchedEffect(Unit) {
        playerViewModel.bindService(context)
        playerViewModel.loadPlaylistAndTrack(trackId)
        playerViewModel.prepareTrack(context)
    }

    LaunchedEffect(mediaPlayer?.currentPosition, isPlaying.value) {
        delay(1000)
        currentPosition.longValue = mediaPlayer?.currentPosition?.toLong() ?: 0
    }

    LaunchedEffect(currentPosition.longValue) {
        sliderPosition.longValue = currentPosition.longValue
        if (currentPosition.longValue / 1000 == totalDuration.longValue / 1000) {
            val currentTrackIndex = playlist.value?.indexOf(track.value)
            val nextTrackIndex = if (currentTrackIndex != playlist.value?.lastIndex)
                currentTrackIndex?.plus(1) else 0
            playerViewModel.track.value =
                nextTrackIndex?.let { playlist.value?.get(it) }
        }
    }

    LaunchedEffect(mediaPlayer?.duration) {
        if ((mediaPlayer?.duration ?: 0) > 0)
            totalDuration.longValue = mediaPlayer?.duration?.toLong() ?: 0
    }

    LaunchedEffect(track.value) {
        playerViewModel.prepareTrack(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            playerViewModel.unBindService(context)
        }
    }

    Column {
        TopScreenRow(navHostController)
        Spacer(modifier = Modifier.size(20.dp))
        track.value?.let {
            TrackInfoColumn(it)
        }
        Spacer(modifier = Modifier.size(20.dp))
        CustomSlider(
            currentPosition,
            sliderPosition,
            totalDuration
        ) {
            currentPosition.longValue = sliderPosition.longValue
            playerViewModel.seekTo(context, sliderPosition.longValue.toInt())
        }
        Spacer(modifier = Modifier.size(20.dp))
        ControlButtons(
            track,
            playlist.value,
            isPlaying,
            playerViewModel,
            context
        )
    }
}

@Composable
fun CustomSlider(
    currentPosition: MutableLongState,
    sliderPosition: MutableLongState,
    totalDuration: MutableLongState,
    onValueChangeFinished: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Slider(
            value = sliderPosition.longValue.toFloat(),
            onValueChange = { sliderPosition.longValue = it.toLong() },
            onValueChangeFinished = { onValueChangeFinished() },
            valueRange = 0f..totalDuration.longValue.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Color.Black,
                activeTrackColor = Color.DarkGray,
                inactiveTrackColor = Color.Gray,
                inactiveTickColor = Color.DarkGray,
                activeTickColor = Color.Black,
            )
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = currentPosition.longValue.convertToText()
            )
            Text(
                text = totalDuration.longValue.convertToText()
            )
        }
    }
}

private fun Long.convertToText(): String {
    val sec = this / 1000
    val minutes = sec / 60
    val seconds = sec % 60

    val minutesString = if (minutes < 10) {
        "0$minutes"
    } else {
        minutes.toString()
    }
    val secondsString = if (seconds < 10) {
        "0$seconds"
    } else {
        seconds.toString()
    }
    return "$minutesString:$secondsString"
}

@Composable
fun ControlButton(icon: Int, size: Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable {
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(size / 1.5f),
            painter = painterResource(id = icon),
            tint = Color.Black,
            contentDescription = null
        )
    }
}


@Composable
fun ControlButtons(
    track: State<Track?>,
    playlist: List<Track>?,
    isPlaying: State<Boolean>,
    playerViewModel: PlayerViewModel,
    context: Context
) {
    val currentIndex = playlist?.indexOf(track.value)

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        ControlButton(icon = R.drawable.ic_previous, size = 40.dp) {
            if (playlist != null) {
                val newIndex = if (currentIndex == 0) playlist.lastIndex else currentIndex?.minus(1)
                playerViewModel.track.value = playlist[newIndex!!]
            }
        }

        Spacer(modifier = Modifier.width(20.dp))
        ControlButton(
            icon = if (isPlaying.value) R.drawable.ic_pause else R.drawable.ic_play,
            size = 100.dp
        ) {
            playerViewModel.playOrPause(context)
        }
        Spacer(modifier = Modifier.width(20.dp))
        ControlButton(icon = R.drawable.ic_next, size = 40.dp) {
            if (playlist != null) {
                val newIndex = if (currentIndex == playlist.lastIndex) 0 else currentIndex?.plus(1)
                playerViewModel.track.value = playlist[newIndex!!]
            }
        }
    }
}

@SuppressLint("LongLogTag")
@Composable
fun TrackInfoColumn(
    track: Track,
    favoritesViewModel: FavoritesViewModel = viewModel(
        factory = AppViewModelProvider.Factory
    )
) {
    val isClicked = remember { mutableStateOf(track.isClicked) }
    Log.d("isClicked Compose", isClicked.value.toString())
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        AsyncImage(
            model = track.album?.coverMedium,
            contentDescription = "picture",
            modifier = Modifier.aspectRatio(1f)
        )
        Spacer(modifier = Modifier.size(20.dp))
        track.titleShort?.let {
            Text(
                text = it,
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
            )
        }
        Spacer(modifier = Modifier.size(10.dp))

        track.artist?.name?.let {
            Text(
                text = it,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.light_grey),
            )
        }
        Spacer(modifier = Modifier.size(10.dp))

        Box(
            modifier = Modifier.wrapContentSize()
        ) {
            Image(
                imageVector = if (isClicked.value) Icons.Filled.CheckCircle else Icons.Filled.AddCircle,
                contentDescription = "add",
                modifier = Modifier
                    .background(
                        color = colorResource(id = R.color.light_grey),
                        shape = CircleShape
                    )
                    .padding(5.dp)
                    .size(15.dp)
                    .clickable {
                        isClicked.value = !isClicked.value
                        track.isClicked = isClicked.value

                        if (isClicked.value) {
                            favoritesViewModel.addTrack(track)
                        } else {
                            favoritesViewModel.deleteTrack(track)
                        }
                    }
            )
        }
    }
}

@Composable
fun TopScreenRow(navHostController: NavHostController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 30.dp)
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .weight(1f)
                .clickable { navHostController.navigateUp() }
        ) {
            Image(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back",
                modifier = Modifier
                    .background(
                        color = colorResource(id = R.color.light_grey),
                        shape = CircleShape
                    )
                    .padding(5.dp)
                    .size(21.dp)
            )
        }

        Text(
            text = stringResource(id = R.string.now_playing),
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}


