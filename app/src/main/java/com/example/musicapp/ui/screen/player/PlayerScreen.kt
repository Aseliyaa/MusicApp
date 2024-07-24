package com.example.musicapp.ui.screen.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.musicapp.data.model.Track
import com.example.musicapp.ui.screen.home.HomeViewModel
import com.example.musicapp.ui.viewmodel.AppViewModelProvider
import kotlinx.coroutines.delay


@Composable
fun PlayerScreen(
    navHostController: NavHostController,
    trackId: String,
    playerViewModel: PlayerViewModel = viewModel(factory = AppViewModelProvider.Factory),
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val mediaPlayer = remember {
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }

    val playlist = remember { mutableStateOf<List<Track>?>(null) }
    val track = remember { mutableStateOf<Track?>(null) }
    val isPrepared = remember { mutableStateOf(false) }
    val isPlaying = remember { mutableStateOf(mediaPlayer.isPlaying) }

    val currentPosition = remember { mutableLongStateOf(0) }
    val sliderPosition = remember { mutableLongStateOf(0) }
    val totalDuration = remember { mutableLongStateOf(0) }

    LaunchedEffect(mediaPlayer.currentPosition, isPlaying.value) {
        delay(1000)
        currentPosition.longValue = mediaPlayer.currentPosition.toLong()
    }

    LaunchedEffect(currentPosition.longValue) {
        sliderPosition.longValue = currentPosition.longValue
    }

    LaunchedEffect(mediaPlayer.duration) {
        if (mediaPlayer.duration > 0)
            totalDuration.longValue = mediaPlayer.duration.toLong()
    }

    LaunchedEffect(Unit) {
        val fetchedPlaylist = homeViewModel.getPlaylist()
        playlist.value = fetchedPlaylist

        for (trackIndex in fetchedPlaylist.indices) {
            if (fetchedPlaylist[trackIndex].id == trackId) {
                track.value = fetchedPlaylist[trackIndex]
                break
            }
        }
    }

    LaunchedEffect(track.value) {
        track.value?.let {
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(it.preview)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    isPrepared.value = true
                    if (isPlaying.value)
                        mediaPlayer.start()
                }
            } catch (e: Exception) {
                Log.e("MediaPlayer", "Error setting data source or preparing", e)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Column {
        TopScreenRow(navHostController)
        Spacer(modifier = Modifier.size(20.dp))
        TrackInfoColumn(track.value)
        Spacer(modifier = Modifier.size(20.dp))
        CustomSlider(
            currentPosition,
            sliderPosition,
            totalDuration,
            mediaPlayer
        )
        Spacer(modifier = Modifier.size(20.dp))
        ControlButtons(
            track,
            playlist.value,
            mediaPlayer,
            isPrepared,
            isPlaying
        )
    }
}

@Composable
fun CustomSlider(
    currentPosition: MutableLongState,
    sliderPosition: MutableLongState,
    totalDuration: MutableLongState,
    mediaPlayer: MediaPlayer
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Slider(
            value = sliderPosition.longValue.toFloat(),
            onValueChange = { sliderPosition.longValue = it.toLong() },
            onValueChangeFinished = {
                currentPosition.longValue = sliderPosition.longValue
                mediaPlayer.seekTo(sliderPosition.longValue.toInt())
            },
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
    track: MutableState<Track?>,
    playlist: List<Track>?,
    player: MediaPlayer,
    isPrepared: MutableState<Boolean>,
    isPlaying: MutableState<Boolean>
) {
    val index = remember {
        mutableStateOf(playlist?.indexOf(track.value))
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        ControlButton(icon = R.drawable.ic_previous, size = 40.dp) {
            if (playlist != null) {
                index.value = playlist.indexOf(track.value)
                index.value = if (index.value == 0) playlist.lastIndex else index.value!! - 1
                track.value = playlist[index.value!!]
            }
        }

        Spacer(modifier = Modifier.width(20.dp))
        ControlButton(
            icon = if (isPlaying.value) R.drawable.ic_pause else R.drawable.ic_play,
            size = 100.dp
        ) {
            if (isPrepared.value) {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.start()
                }
                isPlaying.value = !isPlaying.value
            } else {
                Log.e("MediaPlayer", "MediaPlayer is not prepared")
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        ControlButton(icon = R.drawable.ic_next, size = 40.dp) {
            if (playlist != null) {
                index.value = playlist.indexOf(track.value)
                index.value = if (index.value == playlist.lastIndex) 0 else index.value!! + 1
                track.value = playlist[index.value!!]
            }
        }
    }
}

@Composable
fun TrackInfoColumn(track: Track?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        AsyncImage(
            model = track?.album?.coverMedium,
            contentDescription = "picture",
            modifier = Modifier.aspectRatio(1f)
        )
        Spacer(modifier = Modifier.size(20.dp))
        track?.titleShort?.let {
            Text(
                text = it,
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
            )
        }
        Spacer(modifier = Modifier.size(10.dp))

        track?.artist?.name?.let {
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
                imageVector = Icons.Filled.AddCircle,
                contentDescription = "add",
                modifier = Modifier
                    .background(
                        color = colorResource(id = R.color.light_grey),
                        shape = CircleShape
                    )
                    .padding(5.dp)
                    .size(15.dp)
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


