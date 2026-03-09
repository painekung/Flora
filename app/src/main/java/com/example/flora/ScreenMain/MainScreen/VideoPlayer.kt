package com.example.flora.ScreenMain.MainScreen

import androidx.annotation.OptIn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.flora.R

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    val context = LocalContext.current
    var isMuted by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }



    // สร้างและจำค่า ExoPlayer ไว้
    val exoPlayer = remember {
        // ปรับแต่งการโหลดข้อมูล (Buffer) ให้มากขึ้นเพื่อลดอาการเสียงหายใน Emulator
// ปรับแต่งการโหลดข้อมูล (Buffer) ให้มากขึ้น
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                2500,  // minBufferMs: ขั้นต่ำที่ต้องมีก่อนเริ่มเล่น
                10000, // maxBufferMs: เก็บไว้สูงสุดใน RAM
                1000,  // bufferForPlaybackMs: ต้องมีเพิ่มเท่าไหร่หลังจากกด Resume
                1500   // bufferForPlaybackAfterRebufferMs: ต้องมีเพิ่มเท่าไหร่หลังจากเน็ตหลุด/กระตุก
            )
            .build()

        ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .build().apply {
                // ตั้งค่า Audio Attributes ให้เหมาะสมกับงาน Media
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build()
                setAudioAttributes(audioAttributes, true)

                setMediaItem(MediaItem.fromUri(videoUrl))
                repeatMode = Player.REPEAT_MODE_ALL
                volume = 0f // เริ่มต้นด้วย Mute ตาม Logic ของคุณ
                playWhenReady = true
                prepare()
            }
    }

    // จัดการการเล่น/หยุด เมื่อมีการสลับหน้าหรือซ่อนวิดีโอ
    LaunchedEffect(isVisible) {
        if (isVisible) {
            exoPlayer.play()
            isPlaying = true

            // จุดสำคัญ: บังคับให้ Volume ของ Player ตรงกับตัวแปร isMuted ของเราเสมอเมื่อกลับมาหน้านี้
            exoPlayer.volume = if (isMuted) 0f else 1f
        } else {
            exoPlayer.pause()
            isPlaying = false
            // ไม่ต้องสั่ง mutes ตรงนี้ เพื่อให้มันจำค่าเดิมไว้ตอนเรากลับมา
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop() // หยุดก่อน release
            exoPlayer.release()
        }
    }


    Box(modifier = modifier.fillMaxWidth()) {
        // ส่วนแสดงผลวิดีโอ
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // ซ่อนปุ่มควบคุมมาตรฐานของ ExoPlayer
                    resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },

            modifier = Modifier.fillMaxSize()
        )

        // Overlay สีดำจางๆ
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        // ข้อความกลางหน้าจอ
        Text(
            text = "Over View",
            color = Color.White.copy(alpha = 0.4f),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        // แถบปุ่มควบคุม (Mute/Unmute และ Play/Pause)
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ปุ่มเปิด/ปิดเสียง
            IconButton(
                onClick = {
                    isMuted = !isMuted
                    exoPlayer.volume = if (isMuted) 0f else 1f
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f)),
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

            // ปุ่มเล่น/พักวิดีโอ
            IconButton(
                onClick = {
                    isPlaying = !isPlaying
                    if (isPlaying) exoPlayer.play() else exoPlayer.pause()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f)),
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White
                )
            }
        }
    }
}