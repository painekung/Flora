package com.example.flora.ScreenMain.MainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.flora.R

@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    val context = LocalContext.current
    var isMuted by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose { exoPlayer.release() }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            exoPlayer.play()
            isPlaying = true
        } else {
            exoPlayer.pause()
            exoPlayer.volume = 0f
            isMuted = true
            isPlaying = false

        }


    }

    Box(modifier = modifier
        .fillMaxWidth()

    ) {
        AndroidView(

            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)) // ปรับ alpha 0.0 - 1.0
        )

        Text(
            text = "Over View",
            color = Color.White.copy(alpha = 0.4f),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,

            fontSize = 32.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    isMuted = !isMuted
                    exoPlayer.volume = if (isMuted) 0f else 1f
                },
                modifier = Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.4f)),

            ) {
                Icon(
                    painter = painterResource(
                        id = if (isMuted) R.drawable.mute else R.drawable.voice
                    ),
                    contentDescription = if (isMuted) "Muted" else "Volume",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = {
                    isPlaying = !isPlaying
                    if (isPlaying) exoPlayer.play() else exoPlayer.pause()
                },
                modifier = Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.4f)),
            ) {
                if (isPlaying) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White
                    )
                }
            }
        }
    }
}