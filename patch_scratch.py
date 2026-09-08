import os

with open('app/src/main/java/com/example/ui/screens/ScratchCardScreen.kt', 'w', encoding='utf-8') as f:
    f.write("""package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ScratchCardScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val prefs = context.getSharedPreferences("scratch_prefs", Context.MODE_PRIVATE)

    var scratchesLeft by remember { mutableStateOf(prefs.getInt("scratch_left", 10)) }
    var todayEarned by remember { mutableStateOf(prefs.getInt("scratch_today", 0)) }
    
    val userState by viewModel.userState.collectAsState()
    val coins = userState?.coinBalance ?: 0

    var isScratched by remember { mutableStateOf(false) }
    var scratchPath by remember { mutableStateOf(Path()) }
    var earnedCoins by remember { mutableStateOf(0) }
    var showWinModal by remember { mutableStateOf(false) }
    var showAdModal by remember { mutableStateOf(false) }

    // Scratching logic
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var scratchedArea by remember { mutableFloatStateOf(0f) }

    fun saveState() {
        prefs.edit()
            .putInt("scratch_left", scratchesLeft)
            .putInt("scratch_today", todayEarned)
            .apply()
    }

    // Initialize Random Reward
    LaunchedEffect(scratchesLeft) {
        if (scratchesLeft > 0 && !isScratched) {
            earnedCoins = (listOf(50, 60, 70, 80, 90, 100, 120, 150, 200, 250).random())
        }
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
                .background(Color(0xFFFACC15).copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = (-100).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(Color(0xFF6366F1).copy(alpha = 0.25f), CircleShape)
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
                        text = "Scratch & Win",
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
                        Text("REMAINING SCRATCHES", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text("$scratchesLeft/10", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TODAY'S EARNINGS", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text("+$todayEarned", fontSize = 18.sp, color = Color(0xFF22C55E), fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Scratch Arena
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))))
                        .border(2.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(28.dp))
                        .shadow(30.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    // Hidden Prize Layer
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("🏆", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("+$earnedCoins Coins", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("Congratulations!", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22D3EE))
                    }

                    // Scratchable Canvas Overlay
                    if (scratchesLeft > 0) {
                        AnimatedVisibility(
                            visible = !isScratched,
                            exit = fadeOut(tween(500)),
                            modifier = Modifier.matchParentSize()
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer { alpha = 0.99f }
                                    .pointerInteropFilter { event ->
                                        if (isScratched) return@pointerInteropFilter false
                                        when (event.action) {
                                            MotionEvent.ACTION_DOWN -> {
                                                currentPath = Path().apply { moveTo(event.x, event.y) }
                                                true
                                            }
                                            MotionEvent.ACTION_MOVE -> {
                                                currentPath?.lineTo(event.x, event.y)
                                                scratchPath.addPath(currentPath!!)
                                                currentPath = Path().apply { moveTo(event.x, event.y) }
                                                
                                                scratchedArea += 1f
                                                if (scratchedArea > 200f && !isScratched) {
                                                    isScratched = true
                                                    scope.launch {
                                                        delay(300)
                                                        showWinModal = true
                                                    }
                                                }
                                                true
                                            }
                                            MotionEvent.ACTION_UP -> {
                                                currentPath = null
                                                true
                                            }
                                            else -> false
                                        }
                                    }
                            ) {
                                val w = size.width
                                val h = size.height
                                
                                // Silver Metallic Cover
                                drawRect(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF334155), Color(0xFF64748B), Color(0xFF1E293B)),
                                        start = Offset(0f, 0f),
                                        end = Offset(w, h)
                                    ),
                                    size = size
                                )
                                
                                // Text on Cover
                                drawContext.canvas.nativeCanvas.let { canvas ->
                                    canvas.drawText(
                                        "SCRATCH HERE ✦",
                                        w / 2,
                                        h / 2,
                                        android.graphics.Paint().apply {
                                            color = android.graphics.Color.parseColor("#94A3B8")
                                            textSize = 50f
                                            textAlign = android.graphics.Paint.Align.CENTER
                                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                                        }
                                    )
                                }

                                // Clear scratched path
                                drawPath(
                                    path = scratchPath,
                                    color = Color.Transparent,
                                    style = Stroke(width = 120f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                                    blendMode = BlendMode.Clear
                                )
                            }
                        }
                    } else {
                        // Empty State layer
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0F172A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Scratches Left Today!", fontSize = 16.sp, color = Color(0xFF94A3B8))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Action Area
                if (scratchesLeft > 0) {
                    Text(
                        "👆 Card ko scratch karke apna reward dekhein!", 
                        fontSize = 13.sp, 
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 10.dp)
                    )
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
                            Text("▶ WATCH AD FOR +1 SCRATCH", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
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
                        Text("🪙", fontSize = 38.sp)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("YOU WON! 🎉", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Aapko +$earnedCoins Coins mil gaye hain!", fontSize = 14.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(top = 5.dp))
                    
                    Spacer(modifier = Modifier.height(25.dp))
                    
                    Button(
                        onClick = {
                            viewModel.addCoins(earnedCoins, "Scratch Card Reward")
                            scratchesLeft -= 1
                            todayEarned += earnedCoins
                            saveState()
                            
                            // Reset Scratch for next round
                            isScratched = false
                            scratchPath = Path()
                            scratchedArea = 0f
                            
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
                            Text("COLLECT REWARD", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
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
                    Text("Watch Ad for +1 Scratch", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = {
                            showAdModal = false
                            activity?.let {
                                AdsManager.showRewardedAd(
                                    activity = it,
                                    onRewardEarned = {
                                        scratchesLeft += 1
                                        saveState()
                                        
                                        // Reset scratch view
                                        isScratched = false
                                        scratchPath = Path()
                                        scratchedArea = 0f
                                    },
                                    onAdDismissed = { }
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
"""
    )
