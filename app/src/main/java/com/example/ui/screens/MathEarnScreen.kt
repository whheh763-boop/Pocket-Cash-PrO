package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ads.AdsManager
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay

// Colors Matching the HTML
private val MathBgDark = Color(0xFF030407)
private val MathGlassBg = Color(0xFF161B26).copy(alpha = 0.6f)
private val MathGlassBorder = Color.White.copy(alpha = 0.08f)
private val MathGlassBorderLight = Color.White.copy(alpha = 0.15f)
private val MathPrimary = Color(0xFF6366F1)
private val MathSecondary = Color(0xFF22D3EE)
private val MathGold = Color(0xFFFACC15)
private val MathTextMuted = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathEarnScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uiState by viewModel.userState.collectAsState()

    var num1 by remember { mutableIntStateOf(0) }
    var num2 by remember { mutableIntStateOf(0) }
    var operator by remember { mutableStateOf("+") }
    var correctAnswer by remember { mutableIntStateOf(0) }
    
    var userAnswer by remember { mutableStateOf("") }
    
    var showWinModal by remember { mutableStateOf(false) }
    var showAdModal by remember { mutableStateOf(false) }
    var adTimer by remember { mutableIntStateOf(5) }
    var canSkipAd by remember { mutableStateOf(false) }

    fun generateProblem() {
        val ops = listOf("+", "-", "×")
        operator = ops.random()
        
        when (operator) {
            "+" -> {
                num1 = (10..90).random()
                num2 = (10..90).random()
                correctAnswer = num1 + num2
            }
            "-" -> {
                num1 = (20..100).random()
                num2 = (0..num1).random()
                correctAnswer = num1 - num2
            }
            "×" -> {
                num1 = (2..16).random()
                num2 = (2..11).random()
                correctAnswer = num1 * num2
            }
        }
        userAnswer = ""
    }

    LaunchedEffect(Unit) {
        generateProblem()
    }
    
    // Ad Timer Logic
    LaunchedEffect(showAdModal) {
        if (showAdModal) {
            adTimer = 5
            canSkipAd = false
            while (adTimer > 0) {
                delay(1000)
                adTimer -= 1
            }
            canSkipAd = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MathBgDark)
    ) {
        // Auras
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = (-50).dp)
                .size(250.dp)
                .blur(100.dp)
                .background(MathPrimary.copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = (-100).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(MathSecondary.copy(alpha = 0.25f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.05f))) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = MathSecondary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Math Solver", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                }
                
                // Wallet Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MathGold.copy(alpha = 0.15f))
                        .border(1.dp, MathGold.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MathGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${uiState.coinBalance}", color = MathGold, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                // Reward Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MathGlassBg)
                        .border(1.dp, MathGlassBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sawaal Solve Karein", color = MathTextMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("+50 Coins", color = MathGold, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Math Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(MathGlassBg)
                        .border(1.dp, MathGlassBorderLight, RoundedCornerShape(26.dp))
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Question Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.03f))
                            .border(1.dp, MathGlassBorder, RoundedCornerShape(20.dp))
                            .padding(vertical = 28.dp, horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "SOLVE & EARN", 
                            color = MathSecondary, 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "$num1 $operator $num2 = ?",
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 3.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Input Box
                    OutlinedTextField(
                        value = userAnswer,
                        onValueChange = { if (it.length <= 6) userAnswer = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        placeholder = { 
                            Text("Apna Jawaab Likhein", color = MathTextMuted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) 
                        },
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MathSecondary,
                            unfocusedBorderColor = MathGlassBorderLight,
                            focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            val ans = userAnswer.trim().toIntOrNull()
                            if (ans == null) {
                                Toast.makeText(context, "Kripya jawaab likhein!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (ans == correctAnswer) {
                                viewModel.addCoins(50, "Math Solve Reward")
                                showWinModal = true
                            } else {
                                showAdModal = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(15.dp, RoundedCornerShape(16.dp), spotColor = MathSecondary),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(listOf(MathSecondary, MathPrimary)), 
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("SUBMIT ANSWER", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }

        // Win Modal
        if (showWinModal) {
            Dialog(onDismissRequest = { /* forced action */ }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(MathBgDark.copy(alpha = 0.96f))
                        .border(1.dp, MathGlassBorder, RoundedCornerShape(26.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(MathGold, Color(0xFFD97706))))
                                .shadow(30.dp, CircleShape, spotColor = MathGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(34.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Sahi Jawaab! 🎉", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Aapke wallet me +50 Coins add ho gaye hain!", color = MathTextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = {
                                showWinModal = false
                                generateProblem()
                            },
                            modifier = Modifier.height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(listOf(MathSecondary, MathPrimary)), 
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("AGLA SAWAAL", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        // Ad Modal (Wrong Answer)
        if (showAdModal) {
            Dialog(onDismissRequest = { /* forced action */ }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(MathBgDark.copy(alpha = 0.96f))
                        .border(1.dp, MathGlassBorder, RoundedCornerShape(26.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = MathGold, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Galat Jawaab!", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Agla sawaal unlock karne ke liye ad dekhein!", color = MathTextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (!canSkipAd) {
                            Text("Ad ends in ${adTimer}s...", color = MathSecondary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        } else {
                            Button(
                                onClick = {
                                    // Normally you would trigger RewardedAd here
                                    activity?.let {
                                        AdsManager.showRewardedAd(
                                            activity = it,
                                            onRewardEarned = {
                                                showAdModal = false
                                                generateProblem()
                                            },
                                            onAdDismissed = {
                                                showAdModal = false
                                                generateProblem()
                                            }
                                        )
                                    }
                                },
                                modifier = Modifier.height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(listOf(MathSecondary, MathPrimary)), 
                                            RoundedCornerShape(14.dp)
                                        )
                                        .padding(horizontal = 28.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("WATCH AD & CONTINUE", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
