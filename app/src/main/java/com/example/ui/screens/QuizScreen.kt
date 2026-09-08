package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ads.AdsManager
import com.example.viewmodel.MainViewModel

// Theming Colors (Matching Leaderboard and Refer style)
private val QuizBgDark = Color(0xFF030407)
private val QuizGlassBg = Color(0xFF161B26).copy(alpha = 0.55f)
private val QuizGlassBorder = Color.White.copy(alpha = 0.08f)
private val QuizPrimary = Color(0xFF6366F1)
private val QuizSecondary = Color(0xFF22D3EE)
private val QuizGold = Color(0xFFFACC15)
private val QuizGreen = Color(0xFF10B981)
private val QuizRed = Color(0xFFEF4444)
private val QuizTextMuted = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    
    // Quiz State
    val sessionSize = 10
    var sessionQuestions by remember { mutableStateOf(allNepaliQuizQuestions.shuffled().take(sessionSize)) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var coinsEarned by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }
    var showWrongAnswerDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(QuizBgDark)
    ) {
        // Ambient Auras
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .size(250.dp)
                .blur(100.dp)
                .background(QuizPrimary.copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = (-100).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(QuizSecondary.copy(alpha = 0.25f), CircleShape)
        )

        if (showResults) {
            // Results Screen
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle, 
                    contentDescription = null, 
                    tint = QuizGreen, 
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("Quiz Completed!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(QuizGlassBg)
                        .border(1.dp, QuizGlassBorder, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Score: $score / $sessionSize", color = QuizTextMuted, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = QuizGold, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("+$coinsEarned Coins", color = QuizGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Double Reward Button
                Button(
                    onClick = {
                        activity?.let {
                            AdsManager.showRewardedAd(
                                activity = it,
                                onRewardEarned = {
                                    viewModel.addCoins(coinsEarned, "Quiz Double Reward")
                                    Toast.makeText(context, "Double Rewards Claimed!", Toast.LENGTH_SHORT).show()
                                    // Reset & Play Again
                                    sessionQuestions = allNepaliQuizQuestions.shuffled().take(sessionSize)
                                    currentQuestionIndex = 0
                                    score = 0
                                    coinsEarned = 0
                                    selectedOption = null
                                    isAnswerChecked = false
                                    showResults = false
                                },
                                onAdDismissed = {
                                    Toast.makeText(context, "Ad Closed", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(15.dp, RoundedCornerShape(16.dp), spotColor = QuizSecondary),
                    colors = ButtonDefaults.buttonColors(containerColor = QuizSecondary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Watch Ad for Double Reward (2X)", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Play Again Normal Button
                OutlinedButton(
                    onClick = {
                        sessionQuestions = allNepaliQuizQuestions.shuffled().take(sessionSize)
                        currentQuestionIndex = 0
                        score = 0
                        coinsEarned = 0
                        selectedOption = null
                        isAnswerChecked = false
                        showResults = false
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, QuizGlassBorder)
                ) {
                    Text("Play Again", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = onBack) {
                    Text("Go Home", color = QuizTextMuted)
                }
            }
        } else {
            // Quiz Playing Screen
            val question = sessionQuestions[currentQuestionIndex]
            
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.1f))) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        "प्रश्न ${currentQuestionIndex + 1}/$sessionSize", 
                        color = Color.White, 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(QuizGold.copy(alpha = 0.15f))
                            .border(1.dp, QuizGold.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = QuizGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$coinsEarned", color = QuizGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    // Question Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(QuizGlassBg)
                            .border(1.dp, QuizGlassBorder, RoundedCornerShape(20.dp))
                            .padding(24.dp)
                    ) {
                        Text(
                            question.question,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Options
                    question.options.forEachIndexed { index, option ->
                        val isSelected = selectedOption == index
                        val isCorrectOption = question.answer == index
                        
                        val bgColor by animateColorAsState(
                            targetValue = when {
                                !isAnswerChecked -> if (isSelected) QuizPrimary.copy(alpha = 0.2f) else QuizGlassBg
                                isCorrectOption -> QuizGreen.copy(alpha = 0.2f)
                                isSelected && !isCorrectOption -> QuizRed.copy(alpha = 0.2f)
                                else -> QuizGlassBg
                            },
                            animationSpec = tween(300)
                        )
                        
                        val borderColor by animateColorAsState(
                            targetValue = when {
                                !isAnswerChecked -> if (isSelected) QuizPrimary else QuizGlassBorder
                                isCorrectOption -> QuizGreen
                                isSelected && !isCorrectOption -> QuizRed
                                else -> QuizGlassBorder
                            },
                            animationSpec = tween(300)
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(bgColor)
                                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                                .clickable(enabled = !isAnswerChecked) {
                                    selectedOption = index
                                }
                                .padding(16.dp)
                        ) {
                            Text(
                                option,
                                color = if (isAnswerChecked && isCorrectOption) QuizGreen 
                                        else if (isAnswerChecked && isSelected && !isCorrectOption) QuizRed 
                                        else if (isSelected) Color.White 
                                        else QuizTextMuted,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected || (isAnswerChecked && isCorrectOption)) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Check / Next Button
                    AnimatedVisibility(visible = selectedOption != null) {
                        Button(
                            onClick = {
                                if (!isAnswerChecked) {
                                    isAnswerChecked = true
                                    if (selectedOption == question.answer) {
                                        score++
                                        coinsEarned += 10
                                        viewModel.addCoins(10, "Quiz Answer Correct")
                                        Toast.makeText(context, "+10 Coins!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        showWrongAnswerDialog = true
                                    }
                                } else {
                                    // Move to next
                                    if (currentQuestionIndex < sessionSize - 1) {
                                        currentQuestionIndex++
                                        selectedOption = null
                                        isAnswerChecked = false
                                    } else {
                                        showResults = true
                                        // Show Interstitial on quiz completion
                                        activity?.let { 
                                            AdsManager.showAdIfAvailable(it) {}
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(15.dp, RoundedCornerShape(16.dp), spotColor = QuizPrimary),
                            colors = ButtonDefaults.buttonColors(containerColor = Brush.linearGradient(listOf(QuizPrimary, QuizSecondary)).let { Color.Transparent }),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.linearGradient(listOf(QuizPrimary, QuizSecondary)), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (!isAnswerChecked) "Check Answer" else if (currentQuestionIndex < sessionSize - 1) "Next Question" else "See Results",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        if (showWrongAnswerDialog) {
            WrongAnswerReviveDialog(
                onWatchAd = {
                    activity?.let {
                        AdsManager.showRewardedAd(
                            activity = it,
                            onRewardEarned = {
                                isAnswerChecked = false
                                selectedOption = null
                                showWrongAnswerDialog = false
                                Toast.makeText(context, "Second chance granted!", Toast.LENGTH_SHORT).show()
                            },
                            onAdDismissed = {
                                showWrongAnswerDialog = false
                            }
                        )
                    }
                },
                onSkip = {
                    showWrongAnswerDialog = false
                }
            )
        }
    }
}

@Composable
fun WrongAnswerReviveDialog(onWatchAd: () -> Unit, onSkip: () -> Unit) {
    Dialog(onDismissRequest = onSkip) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(QuizGlassBg)
                .border(1.dp, QuizRed.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(QuizRed.copy(alpha = 0.15f))
                        .border(1.dp, QuizRed.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("😢", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("गलत उत्तर!", color = QuizRed, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "You selected the wrong answer. Want to watch a short ad to try again?",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onWatchAd,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuizPrimary)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Watch Ad to Retry", color = Color.White, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                TextButton(onClick = onSkip) {
                    Text("Skip & Next", color = QuizTextMuted)
                }
            }
        }
    }
}
