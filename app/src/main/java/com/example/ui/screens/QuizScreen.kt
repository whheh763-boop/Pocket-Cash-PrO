package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdsManager
import com.example.viewmodel.MainViewModel

data class GkQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val answer: Int
)

val gkQuestions = listOf(
    GkQuestion(1, "भारत की राजधानी क्या है?", listOf("मुंबई", "नई दिल्ली", "कोलकाता", "जयपुर"), 1),
    GkQuestion(2, "विश्व का सबसे बड़ा महाद्वीप कौन सा है?", listOf("एशिया", "अफ्रीका", "यूरोप", "ऑस्ट्रेलिया"), 0),
    GkQuestion(3, "भारत के 'मिसाइल मैन' के नाम से किसे जाना जाता है?", listOf("डॉ. एपीजे अब्दुल कलाम", "होमी भाभा", "विक्रम साराभाई", "सीवी रमन"), 0),
    GkQuestion(4, "सूर्य के सबसे नजदीकी ग्रह कौन सा है?", listOf("मंगल", "शुक्र", "बुध", "पृथ्वी"), 2),
    GkQuestion(5, "कंप्यूटर का दिमाग (Brain of Computer) किसे कहा जाता है?", listOf("RAM", "CPU", "Hard Disk", "Monitor"), 1),
    GkQuestion(6, "क्षेत्रफल की दृष्टि से भारत का सबसे बड़ा राज्य कौन सा है?", listOf("उत्तर प्रदेश", "राजस्थान", "मध्य प्रदेश", "महाराष्ट्र"), 1),
    GkQuestion(7, "भारत का राष्ट्रीय पशु कौन सा है?", listOf("शेर", "बाघ", "हाथी", "चीता"), 1),
    GkQuestion(8, "रेडियम की खोज किसने की थी?", listOf("मैडम क्यूरी", "आइजैक न्यूटन", "अल्बर्ट आइंस्टीन", "गैलीलियो"), 0),
    GkQuestion(9, "काजीरंगा राष्ट्रीय उद्यान किस राज्य में स्थित है?", listOf("असम", "बिहार", "उत्तराखंड", "राजस्थान"), 0),
    GkQuestion(10, "मानव शरीर में कुल कितनी हड्डियां होती हैं?", listOf("206", "208", "300", "180"), 0)
)

private val QuizBgDark = Color(0xFF030407)
private val QuizGlassBg = Color(0xFF161B26).copy(alpha = 0.55f)
private val QuizGlassBorder = Color.White.copy(alpha = 0.08f)
private val QuizPrimary = Color(0xFF6366F1)
private val QuizSecondary = Color(0xFF22D3EE)
private val QuizGold = Color(0xFFFACC15)
private val QuizGreen = Color(0xFF10B981)
private val QuizRed = Color(0xFFEF4444)
private val QuizTextMuted = Color(0xFF94A3B8)

@Composable
fun QuizScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    
    val prefs = context.getSharedPreferences("GKQuizPrefs", Context.MODE_PRIVATE)
    
    // Load seen IDs
    val seenIdsString = prefs.getString("seen_gk_questions", "") ?: ""
    val initialSeenIds = if (seenIdsString.isEmpty()) mutableSetOf<Int>() else seenIdsString.split(",").mapNotNull { it.toIntOrNull() }.toMutableSet()
    
    var seenIds by remember { mutableStateOf(initialSeenIds) }
    
    val userState by viewModel.userState.collectAsState()
    val userCoins = userState?.coinBalance ?: 0
    
    var currentUnseenQuestions by remember { 
        mutableStateOf(gkQuestions.filter { !seenIds.contains(it.id) }) 
    }
    
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var sessionCoins by remember { mutableStateOf(0) }
    
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }
    var isLifelineUsed by remember { mutableStateOf(false) }
    var hiddenOptions by remember { mutableStateOf(setOf<Int>()) }
    
    var showResults by remember { mutableStateOf(false) }
    
    val isEmptyState = currentUnseenQuestions.isEmpty()

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
                .offset(x = (-50).dp, y = 50.dp)
                .size(250.dp)
                .blur(100.dp)
                .background(QuizSecondary.copy(alpha = 0.2f), CircleShape)
        )

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
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(QuizGlassBg, RoundedCornerShape(12.dp))
                            .border(1.dp, QuizGlassBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("GK Master \uD83E\uDDE0", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Coins
                    Row(
                        modifier = Modifier
                            .background(QuizGold.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("\uD83E\uDE99", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(userCoins.toString(), color = QuizGold, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                    // Score
                    Row(
                        modifier = Modifier
                            .background(QuizPrimary.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(score.toString(), color = QuizPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            }

            if (isEmptyState) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("\uD83C\uDF89", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("शानदार!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Aapne sabhi GK Questions poore kar liye hain. Koi naya question nahi bacha!", textAlign = TextAlign.Center, color = QuizTextMuted, fontSize = 15.sp)
                    
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = {
                            prefs.edit().remove("seen_gk_questions").apply()
                            seenIds = mutableSetOf()
                            currentUnseenQuestions = gkQuestions
                            currentQuestionIndex = 0
                            score = 0
                            sessionCoins = 0
                            showResults = false
                            isAnswerChecked = false
                            selectedOption = null
                            isLifelineUsed = false
                            hiddenOptions = emptySet()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(15.dp, RoundedCornerShape(16.dp), spotColor = QuizSecondary),
                        colors = ButtonDefaults.buttonColors(containerColor = QuizSecondary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Reset History & Replay", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            } else if (showResults) {
                // Results State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("\uD83D\uDCCA", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Game Over!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Aapka Aaj Ka Performance", textAlign = TextAlign.Center, color = QuizTextMuted, fontSize = 15.sp)
                    
                    Spacer(modifier = Modifier.height(30.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(QuizGlassBg, RoundedCornerShape(20.dp))
                                .border(1.dp, QuizGlassBorder, RoundedCornerShape(20.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🏆", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Score", color = QuizTextMuted, fontSize = 14.sp)
                                Text(score.toString(), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(QuizGlassBg, RoundedCornerShape(20.dp))
                                .border(1.dp, QuizGlassBorder, RoundedCornerShape(20.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🪙", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Earned Coins", color = QuizTextMuted, fontSize = 14.sp)
                                Text(sessionCoins.toString(), color = QuizGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(15.dp, RoundedCornerShape(16.dp), spotColor = QuizPrimary),
                        colors = ButtonDefaults.buttonColors(containerColor = QuizPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Agla Round Khelein", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            } else {
                // Active Quiz
                val question = currentUnseenQuestions[currentQuestionIndex]
                
                // Track Seen
                LaunchedEffect(question.id) {
                    if (!seenIds.contains(question.id)) {
                        val newSeen = seenIds.toMutableSet().apply { add(question.id) }
                        seenIds = newSeen
                        prefs.edit().putString("seen_gk_questions", newSeen.joinToString(",")).apply()
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Question Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(QuizGlassBg, RoundedCornerShape(24.dp))
                            .border(1.dp, QuizGlassBorder, RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Question ${currentQuestionIndex + 1}/${currentUnseenQuestions.size}",
                                    color = QuizSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .background(QuizPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("GK Special", color = QuizPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                question.question,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 28.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Options
                    question.options.forEachIndexed { index, option ->
                        if (!hiddenOptions.contains(index)) {
                            val isSelected = selectedOption == index
                            val isCorrectOption = index == question.answer
                            
                            val bgColor by animateColorAsState(
                                targetValue = when {
                                    !isAnswerChecked -> if (isSelected) QuizPrimary.copy(alpha = 0.3f) else QuizGlassBg
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
                            
                            val optionLetter = (65 + index).toChar()
                            
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                if (isSelected || (isAnswerChecked && isCorrectOption)) borderColor else QuizGlassBorder,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            optionLetter.toString(),
                                            color = if (isSelected || (isAnswerChecked && isCorrectOption)) Color.White else QuizTextMuted,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
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
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Lifeline Button
                    if (!isAnswerChecked) {
                        Button(
                            onClick = {
                                if (!isLifelineUsed) {
                                    activity?.let {
                                        AdsManager.showRewardedAd(
                                            activity = it,
                                            onRewardEarned = {
                                                isLifelineUsed = true
                                                val wrongOptions = question.options.indices.filter { idx -> idx != question.answer }.shuffled().take(2)
                                                hiddenOptions = wrongOptions.toSet()
                                                Toast.makeText(context, "50:50 Lifeline Applied!", Toast.LENGTH_SHORT).show()
                                            },
                                            onAdDismissed = {}
                                        )
                                    }
                                }
                            },
                            enabled = !isLifelineUsed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .border(1.dp, if (isLifelineUsed) QuizGlassBorder else QuizSecondary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isLifelineUsed) QuizGlassBg else QuizSecondary.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                if (isLifelineUsed) "⚡ 50:50 Applied" else "🎬 Watch Ad for 50:50 Lifeline",
                                color = if (isLifelineUsed) QuizTextMuted else QuizSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Next / Check Button
                    AnimatedVisibility(visible = selectedOption != null) {
                        Button(
                            onClick = {
                                if (!isAnswerChecked) {
                                    isAnswerChecked = true
                                    if (selectedOption == question.answer) {
                                        score += 10
                                        sessionCoins += 10
                                        viewModel.addCoins(10, "GK Quiz Correct")
                                        Toast.makeText(context, "+10 Coins!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // Next Question
                                    if (currentQuestionIndex < currentUnseenQuestions.size - 1) {
                                        currentQuestionIndex++
                                        selectedOption = null
                                        isAnswerChecked = false
                                        isLifelineUsed = false
                                        hiddenOptions = emptySet()
                                    } else {
                                        showResults = true
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
                            colors = ButtonDefaults.buttonColors(containerColor = QuizPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                if (!isAnswerChecked) "Check Answer" else "Agle Prashn Par Jayein ➔",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
