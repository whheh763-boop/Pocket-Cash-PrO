import re

with open('app/src/main/java/com/example/ui/screens/SpinWheelScreen.kt', 'r') as f:
    content = f.read()

imports = """package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    
    val userState by viewModel.userState.collectAsState()
    val coins = userState.coinBalance
    val freeSpinsLeft = userState.dailyFreeSpinsLeft
    val adSpinsLeft = userState.dailyAdSpinsLeft
    val hasFreeSpins = freeSpinsLeft > 0
    val totalSpinsLeft = freeSpinsLeft + adSpinsLeft

    // The slots mapped exactly to the probabilities requested on backend
    val segments = listOf(
        Pair("0", 0) to Color(0xFFEF4444), // Try Again
        Pair("1", 1) to Color(0xFF6366F1),
        Pair("2", 2) to Color(0xFF22D3EE),
        Pair("5", 5) to Color(0xFFFACC15),
        Pair("10", 10) to Color(0xFF22C55E),
        Pair("0", 0) to Color(0xFFEF4444), // Try Again
        Pair("20", 20) to Color(0xFF3B82F6),
        Pair("50", 50) to Color(0xFFA855F7)
    )

    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var showWinModal by remember { mutableStateOf(false) }
    var showLossModal by remember { mutableStateOf(false) }
    var wonCoins by remember { mutableStateOf(0) }
    
    // Anti-clicker state
    var isSpinCooldown by remember { mutableStateOf(false) }

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
                        text = "Spin & Earn",
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

            // Central Hub
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                
                // Info Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("FREE SPINS", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$freeSpinsLeft/10", color = if(freeSpinsLeft > 0) Color(0xFF22C55E) else Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.White.copy(alpha = 0.1f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("AD SPINS", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$adSpinsLeft/20", color = if(!hasFreeSpins && adSpinsLeft > 0) Color(0xFF38BDF8) else Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))

                // The Wheel UI
                Box(
                    modifier = Modifier
                        .size(320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(25.dp, CircleShape, spotColor = Color(0xFFFACC15).copy(alpha = 0.5f))
                            .background(Color.White.copy(alpha = 0.05f), CircleShape)
                            .border(4.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                            .padding(8.dp)
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .rotate(rotation.value)
                        ) {
                            val sweepAngle = 360f / segments.size
                            segments.forEachIndexed { index, (item, color) ->
                                drawArc(
                                    color = color,
                                    startAngle = index * sweepAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = true,
                                    style = Fill
                                )
                                drawArc(
                                    color = Color.Black.copy(alpha = 0.2f),
                                    startAngle = index * sweepAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = true,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }

                            drawContext.canvas.nativeCanvas.apply {
                                val radius = size.width / 2
                                val centerX = size.width / 2
                                val centerY = size.height / 2
                                segments.forEachIndexed { index, (item, _) ->
                                    val angleInRadians = Math.toRadians((index * sweepAngle + sweepAngle / 2).toDouble())
                                    val textRadius = radius * 0.7f
                                    val x = centerX + textRadius * cos(angleInRadians).toFloat()
                                    val y = centerY + textRadius * sin(angleInRadians).toFloat()

                                    save()
                                    translate(x, y)
                                    rotate((index * sweepAngle + sweepAngle / 2 + 90).toFloat())
                                    val paint = android.graphics.Paint().apply {
                                        color = android.graphics.Color.WHITE
                                        textSize = 65f
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        isFakeBoldText = true
                                        setShadowLayer(5f, 0f, 2f, android.graphics.Color.parseColor("#80000000"))
                                    }
                                    val t = if (item.first == "0") "Try Again" else item.first
                                    if (t == "Try Again") paint.textSize = 40f
                                    drawText(t, 0f, 0f, paint)
                                    restore()
                                }
                            }
                        }
                        
                        // Center dot
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(60.dp)
                                .shadow(15.dp, CircleShape)
                                .background(Brush.radialGradient(listOf(Color.White, Color(0xFFE2E8F0))), CircleShape)
                                .border(4.dp, Color(0xFF94A3B8), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier.size(15.dp).background(Color(0xFF334155), CircleShape))
                        }
                    }

                    // Pointer
                    Canvas(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-15).dp)
                            .size(35.dp)
                    ) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(size.width / 2, size.height)
                            lineTo(0f, 0f)
                            lineTo(size.width, 0f)
                            close()
                        }
                        drawPath(path, Color(0xFFEF4444))
                        drawPath(path, Color.Black.copy(alpha = 0.2f), style = Stroke(width = 2.dp.toPx()))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Spin Action Button
                if (totalSpinsLeft <= 0) {
                     Text("Limit Reached for Today! Come back tomorrow.", color = Color(0xFFEF4444), fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                } else {
                    Button(
                        onClick = {
                            if (isSpinning || isSpinCooldown) return@Button
                            
                            val performSpin = {
                                isSpinning = true
                                isSpinCooldown = true
                                // Call Backend
                                viewModel.performSecureSpin(!hasFreeSpins) { result ->
                                    result.onSuccess { coinsWon ->
                                        wonCoins = coinsWon
                                        
                                        // Calculate slot index based on coin reward mapping
                                        val targetSlotIndex = when(coinsWon) {
                                            1 -> 1
                                            2 -> 2
                                            5 -> 3
                                            10 -> 4
                                            20 -> 6
                                            50 -> 7
                                            else -> listOf(0, 5).random() // Try Again has two slots
                                        }
                                        
                                        val sweepAngle = 360f / segments.size
                                        val randomOffset = (sweepAngle * 0.2f)..(sweepAngle * 0.8f)
                                        // The pointer is at top (-90 degrees visual start)
                                        // We want the chosen slot to land on top. 
                                        // Formula to align a specific slot to Top:
                                        val targetAngle = 360f - (targetSlotIndex * sweepAngle) - (sweepAngle / 2f) + (Math.random().toFloat() * sweepAngle - sweepAngle/2)
                                        val totalRotation = 360f * 5 + targetAngle // 5 full spins

                                        scope.launch {
                                            rotation.animateTo(
                                                targetValue = rotation.value + totalRotation,
                                                animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
                                            )
                                            isSpinning = false
                                            
                                            // 5 second cooldown protection enforced visually
                                            scope.launch {
                                                delay(5000)
                                                isSpinCooldown = false
                                            }
                                            
                                            if (coinsWon > 0) {
                                                showWinModal = true
                                            } else {
                                                showLossModal = true
                                            }
                                        }
                                    }.onFailure { err ->
                                        isSpinning = false
                                        isSpinCooldown = false
                                        Toast.makeText(context, err.message ?: "Error spinning", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            
                            if (hasFreeSpins) {
                                performSpin()
                            } else {
                                // Must watch Ad
                                activity?.let { act ->
                                    AdsManager.showRewardedAd(
                                        activity = act,
                                        onRewardEarned = {
                                            performSpin()
                                        },
                                        onAdDismissed = {
                                            if (!isSpinning) {
                                                Toast.makeText(context, "Ad required to spin!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                } ?: run {
                                    Toast.makeText(context, "Cannot show ad right now", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(55.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        if (hasFreeSpins) listOf(Color(0xFFFACC15), Color(0xFFD97706))
                                        else listOf(Color(0xFF22D3EE), Color(0xFF6366F1))
                                    ),
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (isSpinning) "SPINNING..." else if (hasFreeSpins) "▶ FREE SPIN" else "▶ WATCH AD TO SPIN",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (hasFreeSpins) Color.Black else Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Win Modal
    if (showWinModal) {
        Dialog(onDismissRequest = { showWinModal = false }) {
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
                    Text("Aapko +$wonCoins Coins mil gaye hai!", fontSize = 14.sp, color = Color(0xFF94A3B8), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
                    
                    Spacer(modifier = Modifier.height(25.dp))
                    
                    Button(
                        onClick = { showWinModal = false },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(Color(0xFFFACC15), Color(0xFFD97706))), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AWESOME", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
    
    // Loss Modal
    if (showLossModal) {
        Dialog(onDismissRequest = { showLossModal = false }) {
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
                        onClick = { showLossModal = false },
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

with open('app/src/main/java/com/example/ui/screens/SpinWheelScreen.kt', 'w') as f:
    f.write(imports)
