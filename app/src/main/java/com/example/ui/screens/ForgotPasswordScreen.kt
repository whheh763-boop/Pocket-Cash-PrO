package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MainViewModel
import com.example.ads.AdsManager
import android.app.Activity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // States
    var step by remember { mutableIntStateOf(0) }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    // Verification values
    var captchaText by remember { mutableStateOf(generateCaptcha()) }
    var captchaInput by remember { mutableStateOf("") }
    
    var mathNum1 by remember { mutableIntStateOf(Random.nextInt(10, 50)) }
    var mathNum2 by remember { mutableIntStateOf(Random.nextInt(1, 20)) }
    var mathInput by remember { mutableStateOf("") }
    
    var quizAnswer by remember { mutableStateOf("") }
    val quizQuestion = "What is the capital of France?"
    val correctQuizAnswer = "Paris"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Identity Verification",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            AnimatedContent(targetState = step, label = "forgot_password_steps") { currentStep ->
                when (currentStep) {
                    0 -> {
                        // Step 0: Email Input
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Step 1: Enter your registered Email", color = Color(0xFF94A3B8), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            AuthTextField(
                                value = email,
                                onValueChange = { email = it },
                                placeholder = "Email Address",
                                icon = Icons.Default.Email,
                                keyboardType = KeyboardType.Email
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            PremiumButton("Continue") {
                                if (email.isBlank()) {
                                    Toast.makeText(context, "Enter your email", Toast.LENGTH_SHORT).show()
                                } else {
                                    step = 1
                                }
                            }
                        }
                    }
                    1 -> {
                        // Step 1: Captcha
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Step 2: Solve Captcha to verify you are human", color = Color(0xFF94A3B8), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(captchaText, color = Color(0xFF38BDF8), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 8.sp)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            AuthTextField(
                                value = captchaInput,
                                onValueChange = { captchaInput = it },
                                placeholder = "Enter Captcha",
                                icon = Icons.Default.Email, // Placeholder icon
                                keyboardType = KeyboardType.Text
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            PremiumButton("Verify Captcha") {
                                if (captchaInput.equals(captchaText, ignoreCase = true)) {
                                    step = 2
                                } else {
                                    Toast.makeText(context, "Incorrect Captcha", Toast.LENGTH_SHORT).show()
                                    captchaText = generateCaptcha()
                                    captchaInput = ""
                                }
                            }
                        }
                    }
                    2 -> {
                        // Step 2: Math Solve
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Step 3: Solve Math Question", color = Color(0xFF94A3B8), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text("$mathNum1 + $mathNum2 = ?", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(20.dp))
                            AuthTextField(
                                value = mathInput,
                                onValueChange = { mathInput = it },
                                placeholder = "Answer",
                                icon = Icons.Default.Email, // Placeholder icon
                                keyboardType = KeyboardType.Number
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            PremiumButton("Verify Math") {
                                val ans = mathInput.toIntOrNull()
                                if (ans == (mathNum1 + mathNum2)) {
                                    step = 3
                                } else {
                                    Toast.makeText(context, "Incorrect Answer", Toast.LENGTH_SHORT).show()
                                    mathNum1 = Random.nextInt(10, 50)
                                    mathNum2 = Random.nextInt(1, 20)
                                    mathInput = ""
                                }
                            }
                        }
                    }
                    3 -> {
                        // Step 3: Quiz Verify
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Step 4: Final Quiz Verification", color = Color(0xFF94A3B8), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(quizQuestion, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(20.dp))
                            AuthTextField(
                                value = quizAnswer,
                                onValueChange = { quizAnswer = it },
                                placeholder = "Enter City Name",
                                icon = Icons.Default.Email, // Placeholder icon
                                keyboardType = KeyboardType.Text
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            if (isLoading) {
                                CircularProgressIndicator(color = Color(0xFF38BDF8))
                            } else {
                                PremiumButton("Submit & Send Link") {
                                    if (quizAnswer.equals(correctQuizAnswer, ignoreCase = true)) {
                                        // Process Ad and send Email
                                        coroutineScope.launch {
                                            isLoading = true
                                            try {
                                                val activity = context as? Activity
                                                if (activity != null) {
                                                    AdsManager.showInterstitialAd(activity) {
                                                        // Callback
                                                    }
                                                }
                                                viewModel.sendPasswordReset(email)
                                                step = 4
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Incorrect Answer", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                    4 -> {
                        // Success Step
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight(0.6f)) {
                            Box(modifier = Modifier.size(80.dp).background(Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                                Text("✅", fontSize = 40.sp)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Verification Successful!", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Firebase security automatically protects your account. A secure Password Reset Link has been sent to $email.",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Please check your inbox (and spam folder). Click the link to set your new strong password, then return here to login.",
                                color = Color(0xFF38BDF8),
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(40.dp))
                            PremiumButton("Back to Login") {
                                onBack()
                            }
                        }
                    }
                }
            }
        }
    }
}

fun generateCaptcha(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..6).map { chars.random() }.joinToString("")
}

@Composable
fun PremiumButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .shadow(10.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFF2563EB).copy(alpha = 0.4f)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF0284C7))),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}
