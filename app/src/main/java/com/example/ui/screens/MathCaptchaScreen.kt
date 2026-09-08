package com.example.ui.screens

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
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
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathCaptchaScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    
    val userState by viewModel.userState.collectAsState()
    val coins = userState?.coinBalance ?: 0
    val captchasLeft = userState?.dailyCaptchaLimit ?: 10

    var streak by remember { mutableStateOf(0) }
    var currentCaptchaText by remember { mutableStateOf(generateCaptchaCode()) }
    var answerInput by remember { mutableStateOf("") }
    
    var showWinModal by remember { mutableStateOf(false) }
    var modalMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Solve Captcha", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
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
                        Text("DAILY CAPTCHAS", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text("$captchasLeft/20", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("REWARD / CAPTCHA", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text("+25 Coins", fontSize = 18.sp, color = Color(0xFFFACC15), fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Streak Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFACC15).copy(alpha = 0.05f), RoundedCornerShape(18.dp))
                        .border(1.dp, Color(0xFFFACC15).copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(Color(0xFFFACC15).copy(alpha = 0.15f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("3x Streak Multiplier", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("Solve 3 captchas without error for bonus", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                    }
                    Text("$streak/3", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFACC15))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Captcha Visual Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF1E293B).copy(alpha = 0.7f), Color(0xFF0F172A).copy(alpha = 0.9f))), RoundedCornerShape(24.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                        .shadow(35.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SECURITY VERIFICATION", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF22D3EE), letterSpacing = 0.8.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                    .clickable { 
                                        currentCaptchaText = generateCaptchaCode() 
                                        answerInput = ""
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Visual Canvas area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF020408))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                // Draw noise lines
                                for (i in 0..6) {
                                    val startX = Random.nextFloat() * w
                                    val startY = Random.nextFloat() * h
                                    val endX = Random.nextFloat() * w
                                    val endY = Random.nextFloat() * h
                                    val color = Color(
                                        red = Random.nextFloat(),
                                        green = Random.nextFloat(),
                                        blue = 1f,
                                        alpha = 0.3f
                                    )
                                    drawLine(
                                        color = color,
                                        start = Offset(startX, startY),
                                        end = Offset(endX, endY),
                                        strokeWidth = Random.nextFloat() * 4f + 2f
                                    )
                                }

                                // Draw noise dots
                                for (i in 0..40) {
                                    drawCircle(
                                        color = Color.White.copy(alpha = Random.nextFloat() * 0.4f),
                                        radius = 3f,
                                        center = Offset(Random.nextFloat() * w, Random.nextFloat() * h)
                                    )
                                }

                                // Draw rotated characters using native canvas
                                val textPaint = android.graphics.Paint().apply {
                                    textSize = 80f
                                    typeface = android.graphics.Typeface.MONOSPACE
                                    isFakeBoldText = true
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }

                                val step = w / (currentCaptchaText.length + 1)
                                
                                drawContext.canvas.nativeCanvas.let { nativeCanvas ->
                                    for (i in currentCaptchaText.indices) {
                                        val char = currentCaptchaText[i].toString()
                                        val x = step * (i + 1)
                                        val y = (h / 2) + 25f + (Random.nextFloat() * 20f - 10f)
                                        val angle = Random.nextFloat() * 40f - 20f
                                        
                                        textPaint.color = if (i % 2 == 0) android.graphics.Color.parseColor("#22D3EE") else android.graphics.Color.parseColor("#FACC15")

                                        nativeCanvas.save()
                                        nativeCanvas.translate(x, y)
                                        nativeCanvas.rotate(angle)
                                        nativeCanvas.drawText(char, 0f, 0f, textPaint)
                                        nativeCanvas.restore()
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Text input field
                        OutlinedTextField(
                            value = answerInput,
                            onValueChange = { answerInput = it.uppercase().trim().take(6) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Type text shown above...", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Keyboard, contentDescription = null, tint = Color(0xFF22D3EE))
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF22D3EE),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.8f),
                                unfocusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.8f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.Bold, 
                                fontSize = 18.sp, 
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (answerInput.isEmpty()) {
                                    android.widget.Toast.makeText(context, "Please enter captcha code!", android.widget.Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (captchasLeft <= 0) {
                                    android.widget.Toast.makeText(context, "Daily limit reached!", android.widget.Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                
                                if (answerInput == currentCaptchaText) {
                                    isLoading = true
                                    viewModel.useCaptchaAttempt(
                                        onSuccess = {
                                            activity?.let { act ->
                                                val taskId = java.util.UUID.randomUUID().toString()
                                                viewModel.startSecureTask(taskId)
                                                AdsManager.showRewardedAd(
                                                    activity = act,
                                                    onRewardEarned = {
                                                        var reward = 25
                                                        streak += 1
                                                        if (streak >= 3) {
                                                            reward += 15
                                                            modalMessage = "Streak Completed! Aapko +$reward Coins (+15 Bonus) mil gaye hain."
                                                            streak = 0
                                                        } else {
                                                            modalMessage = "Aapko +$reward Coins credit kar diye gaye hain."
                                                        }
                                                        
                                                        viewModel.claimSecureReward(taskId, reward, "Captcha Reward", 3000L, onSuccess = {
                                                            showWinModal = true
                                                        }, onError = { err ->
                                                            android.widget.Toast.makeText(context, err, android.widget.Toast.LENGTH_SHORT).show()
                                                        })
                                                    },
                                                    onAdDismissed = {
                                                        if (!showWinModal) {
                                                            android.widget.Toast.makeText(context, "No Ad available. Reward requires watching an ad.", android.widget.Toast.LENGTH_SHORT).show()
                                                        }
                                                        currentCaptchaText = generateCaptchaCode()
                                                        answerInput = ""
                                                        isLoading = false
                                                    }
                                                )
                                            } ?: run {
                                                android.widget.Toast.makeText(context, "Cannot show ad.", android.widget.Toast.LENGTH_SHORT).show()
                                                isLoading = false
                                            }
                                        },
                                        onFail = {
                                            android.widget.Toast.makeText(context, "Daily limit reached for Captcha!", android.widget.Toast.LENGTH_SHORT).show()
                                            isLoading = false
                                        }
                                    )
                                } else {
                                    android.widget.Toast.makeText(context, "Incorrect Captcha!", android.widget.Toast.LENGTH_SHORT).show()
                                    streak = 0
                                    currentCaptchaText = generateCaptchaCode()
                                    answerInput = ""
                                }
                            },
                            enabled = answerInput.isNotEmpty() && !isLoading && captchasLeft > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Gray.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.linearGradient(listOf(Color(0xFF22D3EE), Color(0xFF6366F1))), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (isLoading) "VERIFYING..." else "CLAIM COINS NOW", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showWinModal) {
        Dialog(onDismissRequest = { 
            showWinModal = false
            currentCaptchaText = generateCaptchaCode()
            answerInput = ""
            isLoading = false
        }, properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF07090E).copy(alpha = 0.96f), RoundedCornerShape(36.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(36.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).background(Color(0xFF22C55E).copy(alpha = 0.15f), CircleShape).border(1.dp, Color(0xFF22C55E), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(45.dp))
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("VERIFIED SUCCESSFULLY!", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(modalMessage, fontSize = 14.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(top = 5.dp), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(25.dp))
                    Button(
                        onClick = { 
                            showWinModal = false
                            currentCaptchaText = generateCaptchaCode()
                            answerInput = ""
                            isLoading = false
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF22D3EE), Color(0xFF6366F1))), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                            Text("NEXT CAPTCHA", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

fun generateCaptchaCode(): String {
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    return (1..6).map { chars.random() }.joinToString("")
}
