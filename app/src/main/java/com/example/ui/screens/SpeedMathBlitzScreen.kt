package com.example.ui.screens

import android.app.Activity
import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
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
import kotlin.random.Random

@Composable
fun SpeedMathBlitzScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    
    val userState by viewModel.userState.collectAsState()
    val coins = userState?.coinBalance ?: 0

    var score by remember { mutableStateOf(0) }
    val targetScore = 5
    
    var currentIsCorrect by remember { mutableStateOf(true) }
    var displayEquation by remember { mutableStateOf("") }
    
    var timeLeft by remember { mutableFloatStateOf(5.0f) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    
    var showResultModal by remember { mutableStateOf(false) }
    var modalTitle by remember { mutableStateOf("") }
    var modalDesc by remember { mutableStateOf("") }
    var modalSuccess by remember { mutableStateOf(false) }

    fun generateEquation() {
        val ops = listOf("+", "-", "*")
        val op = ops.random()
        val a: Int
        val b: Int
        val realAns: Int
        
        when (op) {
            "+" -> {
                a = Random.nextInt(1, 21)
                b = Random.nextInt(1, 21)
                realAns = a + b
            }
            "-" -> {
                a = Random.nextInt(10, 36)
                b = Random.nextInt(1, a + 1)
                realAns = a - b
            }
            else -> {
                a = Random.nextInt(2, 11)
                b = Random.nextInt(2, 11)
                realAns = a * b
            }
        }
        
        currentIsCorrect = Random.nextBoolean()
        val displayAns = if (currentIsCorrect) {
            realAns
        } else {
            val offset = if (Random.nextBoolean()) Random.nextInt(1, 5) else -Random.nextInt(1, 5)
            realAns + offset
        }
        
        displayEquation = "$a $op $b = $displayAns"
        timeLeft = 5.0f
        isTimerRunning = true
    }

    LaunchedEffect(Unit) {
        generateEquation()
    }

    LaunchedEffect(isTimerRunning) {
        while(isTimerRunning && timeLeft > 0) {
            delay(100)
            timeLeft -= 0.1f
            if (timeLeft <= 0.01f) {
                timeLeft = 0f
                isTimerRunning = false
                isGameOver = true
                modalTitle = "GAME OVER"
                modalDesc = "Time Out! Aapka samay samapt ho gaya."
                modalSuccess = false
                showResultModal = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07090E))
    ) {
        // Ambient Glows
        Box(modifier = Modifier.offset(x = (-40).dp, y = (-40).dp).size(260.dp).blur(100.dp).background(Color(0xFF22D3EE).copy(alpha = 0.25f), CircleShape))
        Box(modifier = Modifier.align(Alignment.BottomEnd).offset(x = 50.dp, y = (-100).dp).size(280.dp).blur(100.dp).background(Color(0xFF6366F1).copy(alpha = 0.25f), CircleShape))

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { isTimerRunning = false; onBack() }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Math Blitz", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
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
                        Text("CURRENT SCORE", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text("$score / $targetScore", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TARGET REWARD", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text("+30 Coins", fontSize = 18.sp, color = Color(0xFFFACC15), fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Game Arena
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF1E293B).copy(alpha = 0.7f), Color(0xFF0F172A).copy(alpha = 0.9f))), RoundedCornerShape(24.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                        .shadow(35.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        
                        // Circular Timer
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                            CircularProgressIndicator(
                                progress = { timeLeft / 5.0f },
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFF22D3EE),
                                strokeWidth = 6.dp,
                                trackColor = Color.White.copy(alpha = 0.08f),
                                gapSize = 0.dp
                            )
                            Text(
                                String.format("%.1f", timeLeft),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Equation Display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF020408), RoundedCornerShape(20.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .padding(vertical = 24.dp, horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                displayEquation,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFFACC15),
                                letterSpacing = 2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        // Action Buttons
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            // False Button
                            Button(
                                onClick = {
                                    if (isGameOver) return@Button
                                    isTimerRunning = false
                                    if (!currentIsCorrect) {
                                        score++
                                        if (score >= targetScore) {
                                            isGameOver = true
                                            modalTitle = "STAGE CLEARED!"
                                            modalDesc = "Aapne 5/5 math problems solve kar liye hain. +30 Coins added!"
                                            modalSuccess = true
                                            showResultModal = true
                                            viewModel.addCoins(30, "Math Blitz Reward")
                                        } else {
                                            generateEquation()
                                        }
                                    } else {
                                        isGameOver = true
                                        modalTitle = "GAME OVER"
                                        modalDesc = "Galat jawab! Phir se koshish karein."
                                        modalSuccess = false
                                        showResultModal = true
                                    }
                                },
                                modifier = Modifier.weight(1f).height(60.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.15f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("FALSE", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFEF4444))
                            }

                            // True Button
                            Button(
                                onClick = {
                                    if (isGameOver) return@Button
                                    isTimerRunning = false
                                    if (currentIsCorrect) {
                                        score++
                                        if (score >= targetScore) {
                                            isGameOver = true
                                            modalTitle = "STAGE CLEARED!"
                                            modalDesc = "Aapne 5/5 math problems solve kar liye hain. +30 Coins added!"
                                            modalSuccess = true
                                            showResultModal = true
                                            viewModel.addCoins(30, "Math Blitz Reward")
                                        } else {
                                            generateEquation()
                                        }
                                    } else {
                                        isGameOver = true
                                        modalTitle = "GAME OVER"
                                        modalDesc = "Galat jawab! Phir se koshish karein."
                                        modalSuccess = false
                                        showResultModal = true
                                    }
                                },
                                modifier = Modifier.weight(1f).height(60.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E).copy(alpha = 0.15f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E))
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("TRUE", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF22C55E))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showResultModal) {
        Dialog(onDismissRequest = { }, properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF07090E).copy(alpha = 0.96f), RoundedCornerShape(36.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(36.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val iconColor = if (modalSuccess) Color(0xFF22C55E) else Color(0xFFEF4444)
                    val iconBg = if (modalSuccess) Color(0xFF22C55E).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
                    val theIcon = if (modalSuccess) Icons.Default.EmojiEvents else Icons.Default.Cancel

                    Box(modifier = Modifier.size(80.dp).background(iconBg, CircleShape).border(1.dp, iconColor, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(theIcon, contentDescription = null, tint = iconColor, modifier = Modifier.size(45.dp))
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(modalTitle, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(modalDesc, fontSize = 14.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(top = 5.dp), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(25.dp))
                    Button(
                        onClick = { 
                            showResultModal = false
                            score = 0
                            isGameOver = false
                            generateEquation()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF22D3EE), Color(0xFF6366F1))), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                            Text("PLAY AGAIN", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
