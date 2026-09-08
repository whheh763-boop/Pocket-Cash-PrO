import os

with open('app/src/main/java/com/example/ui/screens/SpinWheelScreen.kt', 'w', encoding='utf-8') as f:
    f.write("""package com.example.ui.screens

import android.app.Activity
import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ads.AdsManager
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinWheelScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val prefs = context.getSharedPreferences("spin_prefs", Context.MODE_PRIVATE)

    var spinsLeft by remember { mutableStateOf(prefs.getInt("spin_left", 10)) }
    var todayEarned by remember { mutableStateOf(prefs.getInt("spin_today", 0)) }
    
    val userState by viewModel.userState.collectAsState()
    val coins = userState?.coins ?: 0

    val segments = listOf(
        Pair("50", 50) to Color(0xFF6366F1),
        Pair("100", 100) to Color(0xFF0F172A),
        Pair("20", 20) to Color(0xFF22D3EE),
        Pair("200", 200) to Color(0xFFFACC15),
        Pair("10", 10) to Color(0xFF6366F1),
        Pair("150", 150) to Color(0xFF0F172A),
        Pair("30", 30) to Color(0xFF22D3EE),
        Pair("500", 500) to Color(0xFF22C55E)
    )

    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var showWinModal by remember { mutableStateOf(false) }
    var showAdModal by remember { mutableStateOf(false) }
    var wonCoins by remember { mutableStateOf(0) }
    var adTimer by remember { mutableStateOf(5) }

    fun saveState() {
        prefs.edit()
            .putInt("spin_left", spinsLeft)
            .putInt("spin_today", todayEarned)
            .apply()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07090E))
    ) {
        // Ambient Background Glows
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = (-40).dp)
                .size(260.dp)
                .blur(100.dp)
                .background(Color(0xFF6366F1).copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = (-100).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(Color(0xFFFACC15).copy(alpha = 0.25f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Spin & Win",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                // Wallet Badge
                Row(
                    modifier = Modifier
                        .background(Color(0xFFFACC15).copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFFFACC15).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🪙 ${"%,d".format(coins)}",
                        color = Color(0xFFFACC15),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stats Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161B26).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("SPINS REMAINING", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text("$spinsLeft/10", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TODAY'S WON", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text("+$todayEarned", fontSize = 18.sp, color = Color(0xFF22C55E), fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Wheel Arena
                Box(
                    modifier = Modifier
                        .size(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Wheel Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(6.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                            .shadow(35.dp, CircleShape, spotColor = Color(0xFF6366F1).copy(alpha = 0.3f))
                            .clip(CircleShape)
                            .rotate(rotation.value)
                    ) {
                        val numSegments = segments.size
                        val sweepAngle = 360f / numSegments
                        val radius = size.width / 2

                        for (i in 0 until numSegments) {
                            val segmentInfo = segments[i]
                            val label = segmentInfo.first.first
                            val color = segmentInfo.second

                            // Segment Fill
                            drawArc(
                                color = color,
                                startAngle = i * sweepAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                style = Fill
                            )

                            // Segment Dividers
                            drawArc(
                                color = Color.White.copy(alpha = 0.15f),
                                startAngle = i * sweepAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Segment Text
                            val angleInRadians = Math.toRadians((i * sweepAngle + sweepAngle / 2).toDouble())
                            val textRadius = radius * 0.7f
                            val x = (center.x + textRadius * cos(angleInRadians)).toFloat()
                            val y = (center.y + textRadius * sin(angleInRadians)).toFloat()

                            drawContext.canvas.nativeCanvas.let { nativeCanvas ->
                                nativeCanvas.save()
                                nativeCanvas.rotate((i * sweepAngle + sweepAngle / 2 + 90f), x, y)
                                nativeCanvas.drawText(
                                    "$label C",
                                    x,
                                    y + 5f,
                                    android.graphics.Paint().apply {
                                        this.color = if (color == Color(0xFFFACC15)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                                        textSize = 45f
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                                    }
                                )
                                nativeCanvas.restore()
                            }
                        }
                    }

                    // Center Cap
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF0F172A), CircleShape)
                            .border(3.dp, Color(0xFFFACC15), CircleShape)
                            .shadow(20.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚡", fontSize = 24.sp) // Fallback for bolt icon
                    }
                    
                    // Top Pointer
                    Canvas(modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-12).dp)
                        .size(32.dp, 28.dp)
                    ) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width / 2, size.height)
                            close()
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFFFACC15)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Action Buttons
                if (spinsLeft > 0) {
                    Button(
                        onClick = {
                            if (isSpinning) return@Button
                            isSpinning = true
                            
                            // Play sound
                            scope.launch { 
                                val tg = android.media.ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 100)
                                for(i in 1..15) { 
                                    tg.startTone(android.media.ToneGenerator.TONE_CDMA_PIP, 50)
                                    delay(200) 
                                }
                                tg.release() 
                            }

                            scope.launch {
                                val winningIndex = (0 until segments.size).random()
                                val selectedPrize = segments[winningIndex]
                                
                                val degreesPerSegment = 360f / segments.size
                                val targetSegmentAngle = (segments.size - winningIndex) * degreesPerSegment - (degreesPerSegment / 2)
                                
                                val extraSpins = 5 * 360f
                                val targetRotation = rotation.value + extraSpins + (targetSegmentAngle - (rotation.value % 360f))

                                rotation.animateTo(
                                    targetValue = targetRotation,
                                    animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing)
                                )

                                wonCoins = selectedPrize.first.second
                                isSpinning = false
                                showWinModal = true
                            }
                        },
                        enabled = !isSpinning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFFFACC15), Color(0xFFD97706))),
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("▶ SPIN NOW", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                } else {
                    Button(
                        onClick = { showAdModal = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF22D3EE), Color(0xFF6366F1))),
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("▶ WATCH AD FOR +1 SPIN", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }

    // Win Modal
    if (showWinModal) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF07090E).copy(alpha = 0.96f), RoundedCornerShape(36.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(36.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFFFACC15).copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, Color(0xFFFACC15), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏆", fontSize = 38.sp)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("YOU WON! 🎉", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Aapko +$wonCoins Coins mil gaye hai!", fontSize = 14.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(top = 5.dp))
                    
                    Spacer(modifier = Modifier.height(25.dp))
                    
                    Button(
                        onClick = {
                            viewModel.addCoins(wonCoins, "Spin Wheel Reward")
                            spinsLeft -= 1
                            todayEarned += wonCoins
                            saveState()
                            showWinModal = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(Color(0xFFFACC15), Color(0xFFD97706))), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("COLLECT COINS", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }

    // Ad Modal
    if (showAdModal) {
        Dialog(
            onDismissRequest = { showAdModal = false }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF07090E).copy(alpha = 0.96f), RoundedCornerShape(36.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(36.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📺", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Watch Ad for +1 Spin", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = {
                            showAdModal = false
                            activity?.let {
                                AdsManager.showRewardedAd(
                                    activity = it,
                                    onRewardEarned = {
                                        spinsLeft += 1
                                        saveState()
                                    },
                                    onAdDismissed = {
                                        // Optional: Handle ad dismissed without earning
                                    }
                                )
                            } ?: run {
                                android.widget.Toast.makeText(context, "Cannot show ad.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(Color(0xFF22D3EE), Color(0xFF6366F1))), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("WATCH NOW", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
""")
