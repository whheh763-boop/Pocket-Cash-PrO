package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdsManager
import com.example.viewmodel.MainViewModel

@Composable
fun WatchVideoScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    
    var isWatching by remember { mutableStateOf(false) }

    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07090E))
    ) {
        // Ambient Glows
        Box(
            modifier = Modifier
                .offset(x = (-20).dp, y = 50.dp)
                .size(220.dp)
                .blur(90.dp)
                .background(Color(0xFF7C3AED).copy(alpha = 0.35f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 30.dp, y = (-80).dp)
                .size(200.dp)
                .blur(90.dp)
                .background(Color(0xFF3B82F6).copy(alpha = 0.35f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (!isWatching) onBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Watch & Earn", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = (-0.3).sp)
            }

            // Main Content Wrapper
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                // Reward Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161B26).copy(alpha = 0.65f), RoundedCornerShape(28.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(28.dp))
                        .shadow(40.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                        .padding(top = 36.dp, bottom = 30.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing Video Icon
                    Box(
                        modifier = Modifier
                            .padding(bottom = 28.dp)
                            .size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pulse Ring
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(pulseScale)
                                .background(Color(0xFF8B5CF6).copy(alpha = pulseAlpha), CircleShape)
                        )
                        // Inner Play Icon
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))), CircleShape)
                                .shadow(25.dp, CircleShape, spotColor = Color(0xFF8B5CF6).copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp).offset(x = 2.dp))
                        }
                    }

                    Text(
                        text = "Watch short video ads to earn free coins!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "You can earn ${viewModel.appConfig.value.videoReward} coins for every video you watch. Make sure to watch the video until the end to receive your reward.",
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 30.dp)
                    )

                    Button(
                        onClick = {
                            if (activity != null) {
                                isWatching = true
                                val taskId = java.util.UUID.randomUUID().toString()
                                viewModel.startSecureTask(taskId)
                                AdsManager.showRewardedAd(
                                    activity = activity,
                                    onRewardEarned = {
                                        viewModel.claimSecureReward(taskId, viewModel.appConfig.value.videoReward, "Video Reward", 8000L, onSuccess = {
                                            android.widget.Toast.makeText(context, "You earned ${viewModel.appConfig.value.videoReward} coins!", android.widget.Toast.LENGTH_SHORT).show()
                                        }, onError = { err ->
                                            android.widget.Toast.makeText(context, err, android.widget.Toast.LENGTH_SHORT).show()
                                        })
                                    },
                                    onAdDismissed = {
                                        isWatching = false
                                    }
                                )
                            }
                        },
                        enabled = !isWatching,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(if (isWatching) Color(0xFF6D28D9) else Color(0xFF8B5CF6), Color(0xFF6D28D9))), RoundedCornerShape(18.dp))
                                .border(if (isWatching) 1.dp else 0.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isWatching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.5.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("INITIALIZING AD...", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .background(Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(14.dp).offset(x = 1.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("WATCH AD (+${viewModel.appConfig.value.videoReward} COINS)", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
