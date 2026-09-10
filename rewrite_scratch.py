import re

content = """package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ads.AdsManager
import com.example.viewmodel.MainViewModel

@Composable
fun ScratchCardScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    
    val userState by viewModel.userState.collectAsState()
    val coins = userState.coinBalance
    val freeScratchLeft = userState.dailyFreeScratchLeft
    val adScratchLeft = userState.dailyAdScratchLeft
    val hasFreeScratch = freeScratchLeft > 0
    val totalScratchLeft = freeScratchLeft + adScratchLeft

    // State for the card flow
    var isCardActive by remember { mutableStateOf(false) }
    var currentTaskId by remember { mutableStateOf<String?>(null) }
    var currentReward by remember { mutableStateOf(0) }
    
    var showWinModal by remember { mutableStateOf(false) }
    var showLossModal by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Scratch logic state
    var scratchedCells by remember { mutableStateOf(mutableSetOf<Pair<Int, Int>>()) }
    var paths by remember { mutableStateOf(mutableListOf<Path>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var isScratched by remember { mutableStateOf(false) }
    val gridSize = 10
    val scratchThreshold = 70 // 70% of the grid

    fun resetCard() {
        isCardActive = false
        currentTaskId = null
        currentReward = 0
        scratchedCells = mutableSetOf()
        paths = mutableListOf()
        currentPath = null
        isScratched = false
    }

    fun requestCard(isAd: Boolean) {
        isLoading = true
        viewModel.generateSecureScratchCard(isAdScratch = isAd) { result ->
            isLoading = false
            result.onSuccess { pair ->
                currentTaskId = pair.first
                currentReward = pair.second
                isCardActive = true
            }.onFailure { err ->
                Toast.makeText(context, err.message ?: "Failed to generate card", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onScratchComplete() {
        if (isScratched || currentTaskId == null) return
        isScratched = true
        
        // Finalize claim on backend
        viewModel.claimSecureScratchReward(currentTaskId!!) { result ->
            result.onSuccess { actualReward ->
                if (actualReward > 0) {
                    showWinModal = true
                } else {
                    showLossModal = true
                }
            }.onFailure { err ->
                Toast.makeText(context, err.message ?: "Claim failed!", Toast.LENGTH_SHORT).show()
                resetCard() // Reset so they can try again if failed due to fraud check etc
            }
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
                .background(Color(0xFF22C55E).copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = (-100).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(Color(0xFF8B5CF6).copy(alpha = 0.25f), CircleShape)
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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

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

            // Info Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("FREE CARDS", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$freeScratchLeft/10", color = if(freeScratchLeft > 0) Color(0xFF22C55E) else Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.White.copy(alpha = 0.1f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("AD CARDS", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$adScratchLeft/20", color = if(!hasFreeScratch && adScratchLeft > 0) Color(0xFF38BDF8) else Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            // Interactive Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!isCardActive) {
                    if (totalScratchLeft <= 0) {
                        Text("Limit Reached for Today! Come back tomorrow.", color = Color(0xFFEF4444), fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    } else if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF22C55E))
                    } else {
                        // Buy Card Button
                        Button(
                            onClick = {
                                if (hasFreeScratch) {
                                    requestCard(isAd = false)
                                } else {
                                    activity?.let { act ->
                                        AdsManager.showRewardedAd(
                                            activity = act,
                                            onRewardEarned = {
                                                requestCard(isAd = true)
                                            },
                                            onAdDismissed = {}
                                        )
                                    } ?: run {
                                        Toast.makeText(context, "Cannot show ad right now", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            if (hasFreeScratch) listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                                            else listOf(Color(0xFF38BDF8), Color(0xFF8B5CF6))
                                        ),
                                        RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (hasFreeScratch) "GET FREE SCRATCH CARD" else "WATCH AD FOR CARD",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else {
                    // Active Scratch Card UI
                    Box(
                        modifier = Modifier
                            .size(300.dp, 300.dp)
                            .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF22C55E).copy(alpha = 0.3f))
                            .background(Color(0xFF1E293B), RoundedCornerShape(24.dp))
                            .border(2.dp, Color(0xFF334155), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // The actual reward underneath
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (currentReward > 0) {
                                Text("🏆", fontSize = 50.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("YOU WON", fontSize = 18.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                Text("+$currentReward Coins", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFACC15))
                            } else {
                                Text("😢", fontSize = 50.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Better Luck", fontSize = 18.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                Text("Next Time", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }

                        // Scratch Overlay Layer
                        AnimatedVisibility(
                            visible = !isScratched,
                            exit = fadeOut(animationSpec = tween(500))
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp))
                                    .pointerInput(Unit) {
                                        detectDragGestures(
                                            onDragStart = { offset ->
                                                val path = Path().apply {
                                                    moveTo(offset.x, offset.y)
                                                }
                                                currentPath = path
                                                paths.add(path)
                                            },
                                            onDragEnd = {
                                                currentPath = null
                                                if (scratchedCells.size >= scratchThreshold && !isScratched) {
                                                    onScratchComplete()
                                                }
                                            },
                                            onDrag = { change, _ ->
                                                currentPath?.lineTo(change.position.x, change.position.y)
                                                
                                                val cellX = (change.position.x / size.width * gridSize).toInt()
                                                val cellY = (change.position.y / size.height * gridSize).toInt()
                                                
                                                if (cellX in 0 until gridSize && cellY in 0 until gridSize) {
                                                    val newSet = scratchedCells.toMutableSet()
                                                    newSet.add(Pair(cellX, cellY))
                                                    scratchedCells = newSet
                                                    
                                                    // Also check threshold during drag for instant win feel
                                                    if (scratchedCells.size >= scratchThreshold && !isScratched) {
                                                        onScratchComplete()
                                                    }
                                                }
                                            }
                                        )
                                    }
                            ) {
                                // Draw base scratch layer
                                drawRect(
                                    brush = Brush.linearGradient(listOf(Color(0xFF94A3B8), Color(0xFF475569)))
                                )

                                // Add some pattern to make it look like a scratch card
                                val patternPaint = androidx.compose.ui.graphics.Paint().apply {
                                    color = Color.White.copy(alpha = 0.1f)
                                }
                                for (i in 0..10) {
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.1f),
                                        start = Offset(0f, i * 100f),
                                        end = Offset(size.width, i * 100f - 200f),
                                        strokeWidth = 20f
                                    )
                                }

                                // Apply clear blend mode to the user's paths
                                drawContext.canvas.saveLayer(size.toRect(), androidx.compose.ui.graphics.Paint())
                                drawRect(Color(0xFF64748B)) // Solid overlay on top of layer
                                
                                val clearPaint = androidx.compose.ui.graphics.Paint().apply {
                                    color = Color.Transparent
                                    blendMode = BlendMode.Clear
                                    style = androidx.compose.ui.graphics.PaintingStyle.Stroke
                                    strokeWidth = 80f // Brush size
                                    strokeCap = StrokeCap.Round
                                    strokeJoin = StrokeJoin.Round
                                }
                                
                                paths.forEach { path ->
                                    drawContext.canvas.drawPath(path, clearPaint)
                                }
                                drawContext.canvas.restore()
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Scratch the card to reveal your reward", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    Text("Progress: ${scratchedCells.size}%", color = Color(0xFF22C55E), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }

    // Win Modal
    if (showWinModal) {
        Dialog(onDismissRequest = { 
            showWinModal = false
            resetCard()
        }) {
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
                            .background(Color(0xFF22C55E).copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, Color(0xFF22C55E), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎉", fontSize = 38.sp)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("YOU WON!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Aapko +$currentReward Coins mil gaye hai!", fontSize = 14.sp, color = Color(0xFF94A3B8), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
                    
                    Spacer(modifier = Modifier.height(25.dp))
                    
                    Button(
                        onClick = { 
                            showWinModal = false 
                            resetCard()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(Color(0xFF22C55E), Color(0xFF16A34A))), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AWESOME", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
    
    // Loss Modal
    if (showLossModal) {
        Dialog(onDismissRequest = { 
            showLossModal = false
            resetCard()
        }) {
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
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, Color(0xFFEF4444), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("😢", fontSize = 38.sp)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("TRY AGAIN!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Oops! Koi coins nahi mile. Agli baar try karein.", fontSize = 14.sp, color = Color(0xFF94A3B8), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
                    
                    Spacer(modifier = Modifier.height(25.dp))
                    
                    Button(
                        onClick = { 
                            showLossModal = false 
                            resetCard()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF1E293B))), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("CLOSE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
"""

with open('app/src/main/java/com/example/ui/screens/ScratchCardScreen.kt', 'w') as f:
    f.write(content)
