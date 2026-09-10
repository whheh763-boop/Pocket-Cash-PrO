import re

content = """package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ads.AdsManager
import com.example.model.SecureQuizData
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SecureQuizTemplate(
    viewModel: MainViewModel,
    quizType: String, // "MATH" or "GK"
    title: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    
    val userState by viewModel.userState.collectAsState()
    val coins = userState.coinBalance
    
    val freeLeft = if (quizType == "MATH") userState.dailyFreeMathQuizLeft else userState.dailyFreeGkQuizLeft
    val adLeft = if (quizType == "MATH") userState.dailyAdMathQuizLeft else userState.dailyAdGkQuizLeft
    val hasFree = freeLeft > 0
    val totalLeft = freeLeft + adLeft

    var currentQuestion by remember { mutableStateOf<SecureQuizData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Timer state
    var timeLeft by remember { mutableStateOf(15) }
    var isTimerRunning by remember { mutableStateOf(false) }
    
    var showWinModal by remember { mutableStateOf(false) }
    var showLossModal by remember { mutableStateOf(false) }

    fun resetState() {
        currentQuestion = null
        timeLeft = 15
        isTimerRunning = false
    }

    fun requestQuestion(isAd: Boolean) {
        isLoading = true
        viewModel.generateSecureQuiz(quizType, isAd) { result ->
            isLoading = false
            result.onSuccess { qData ->
                currentQuestion = qData
                timeLeft = 15
                isTimerRunning = true
            }.onFailure { err ->
                Toast.makeText(context, err.message ?: "Failed to get question", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    fun submitAnswer(selectedIndex: Int, isTimeout: Boolean = false) {
        if (currentQuestion == null) return
        isTimerRunning = false
        val taskId = currentQuestion!!.id
        
        val doSubmit = {
            viewModel.submitSecureQuizAnswer(taskId, selectedIndex) { result ->
                result.onSuccess { isCorrect ->
                    if (isCorrect) {
                        showWinModal = true
                    } else {
                        showLossModal = true
                    }
                }.onFailure { err ->
                    Toast.makeText(context, err.message ?: "Submission failed!", Toast.LENGTH_SHORT).show()
                    resetState()
                }
            }
        }
        
        // Show Interstitial Ad before result
        activity?.let { act ->
            AdsManager.showInterstitialAd(
                activity = act,
                onAdDismissed = { doSubmit() }
            )
        } ?: run {
            doSubmit()
        }
    }

    // Timer coroutine
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft <= 0) {
                // Timeout
                submitAnswer(-1, true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07090E))
    ) {
        // Background effects
        Box(modifier = Modifier.offset(x = (-40).dp, y = 50.dp).size(260.dp).blur(100.dp).background(Color(0xFF3B82F6).copy(alpha = 0.2f), CircleShape))
        Box(modifier = Modifier.align(Alignment.BottomEnd).offset(x = 50.dp, y = (-100).dp).size(280.dp).blur(100.dp).background(Color(0xFF8B5CF6).copy(alpha = 0.2f), CircleShape))

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
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
                    Text(title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }

                Row(
                    modifier = Modifier
                        .background(Color(0xFFFACC15).copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFFFACC15).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🪙 ${"%,d".format(coins)}", color = Color(0xFFFACC15), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }
            }

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("FREE", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("$freeLeft/10", color = if(hasFree) Color(0xFF22C55E) else Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.White.copy(alpha = 0.1f)))
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("AD EXTRA", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("$adLeft/15", color = if(!hasFree && adLeft > 0) Color(0xFF38BDF8) else Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            // Game Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (currentQuestion == null) {
                    if (totalLeft <= 0) {
                        Text("Limit Reached for Today!", color = Color(0xFFEF4444), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF3B82F6))
                    } else {
                        Button(
                            onClick = {
                                if (hasFree) {
                                    requestQuestion(false)
                                } else {
                                    activity?.let { act ->
                                        AdsManager.showRewardedAd(
                                            activity = act,
                                            onRewardEarned = { requestQuestion(true) },
                                            onAdDismissed = {}
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.8f).height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            if (hasFree) listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
                                            else listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
                                        ),
                                        RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (hasFree) "START QUIZ" else "WATCH AD TO PLAY", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }
                    }
                } else {
                    // Active Question UI
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "00:${timeLeft.toString().padStart(2, '0')}",
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(30.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(24.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                            .padding(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentQuestion!!.question,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(30.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        currentQuestion!!.options.forEachIndexed { index, option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                    .clickable { submitAnswer(index) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(Color.White.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${('A' + index)}", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(option, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Win Modal
    if (showWinModal) {
        Dialog(onDismissRequest = { 
            showWinModal = false
            resetState()
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
                    Box(modifier = Modifier.size(80.dp).background(Color(0xFF22C55E).copy(alpha = 0.15f), CircleShape).border(1.dp, Color(0xFF22C55E), CircleShape), contentAlignment = Alignment.Center) {
                        Text("🎉", fontSize = 38.sp)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("CORRECT!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Aapko +2 Coins mil gaye hai!", fontSize = 14.sp, color = Color(0xFF94A3B8), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
                    
                    Spacer(modifier = Modifier.height(25.dp))
                    
                    Button(onClick = { showWinModal = false; resetState() }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))) {
                        Text("AWESOME", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
    
    // Loss Modal
    if (showLossModal) {
        Dialog(onDismissRequest = { 
            showLossModal = false
            resetState()
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
                    Box(modifier = Modifier.size(80.dp).background(Color(0xFFEF4444).copy(alpha = 0.15f), CircleShape).border(1.dp, Color(0xFFEF4444), CircleShape), contentAlignment = Alignment.Center) {
                        Text("😢", fontSize = 38.sp)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("WRONG / TIMEOUT", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Oops! Koi coins nahi mile.", fontSize = 14.sp, color = Color(0xFF94A3B8), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
                    
                    Spacer(modifier = Modifier.height(25.dp))
                    
                    Button(onClick = { showLossModal = false; resetState() }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))) {
                        Text("CLOSE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
"""

with open('app/src/main/java/com/example/ui/screens/SecureQuizTemplate.kt', 'w') as f:
    f.write(content)
