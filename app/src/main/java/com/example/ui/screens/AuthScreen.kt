package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Country
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// --- Husky Shapes ---
class TriangleShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

class HeadShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = 0f, top = 0f, right = size.width, bottom = size.height,
                    topLeftCornerRadius = androidx.compose.ui.geometry.CornerRadius(90f, 90f),
                    topRightCornerRadius = androidx.compose.ui.geometry.CornerRadius(90f, 90f),
                    bottomLeftCornerRadius = androidx.compose.ui.geometry.CornerRadius(70f, 70f),
                    bottomRightCornerRadius = androidx.compose.ui.geometry.CornerRadius(70f, 70f)
                )
            )
        }
        return Outline.Generic(path)
    }
}

class NoseShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = 0f, top = 0f, right = size.width, bottom = size.height,
                    topLeftCornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f),
                    topRightCornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f),
                    bottomLeftCornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f),
                    bottomRightCornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
                )
            )
        }
        return Outline.Generic(path)
    }
}

@Composable
fun AnimatedHusky() {
    val infiniteTransition = rememberInfiniteTransition(label = "husky_float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "husky_offset"
    )

    Box(
        modifier = Modifier
            .offset { androidx.compose.ui.unit.IntOffset(0, offsetY.roundToInt()) }
            .size(170.dp),
        contentAlignment = Alignment.Center
    ) {
        // Left Ear
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = (-25).dp)
                .size(width = 55.dp, height = 72.dp)
                .background(Brush.linearGradient(listOf(Color(0xFF8895AA), Color(0xFF343E50))), TriangleShape())
                .clip(TriangleShape()),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .size(width = 30.dp, height = 45.dp)
                    .background(Color(0xFFE28A9D).copy(alpha = 0.8f), TriangleShape())
            )
        }
        
        // Right Ear
        Box(
            modifier = Modifier
                .offset(x = 40.dp, y = (-25).dp)
                .size(width = 55.dp, height = 72.dp)
                .background(Brush.linearGradient(listOf(Color(0xFF8895AA), Color(0xFF343E50))), TriangleShape())
                .clip(TriangleShape()),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .size(width = 30.dp, height = 45.dp)
                    .background(Color(0xFFE28A9D).copy(alpha = 0.8f), TriangleShape())
            )
        }

        // Head
        Box(
            modifier = Modifier
                .offset(y = 15.dp)
                .size(width = 150.dp, height = 115.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF2B3545), Color(0xFF1E2633))), HeadShape())
                .border(1.dp, Color.White.copy(alpha = 0.1f), HeadShape()),
            contentAlignment = Alignment.Center
        ) {
            // Left Eye
            Box(
                modifier = Modifier
                    .offset(x = (-25).dp, y = (-5).dp)
                    .size(24.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(10.dp).shadow(6.dp, CircleShape, spotColor = Color(0xFF38BDF8)).background(Color(0xFF38BDF8), CircleShape))
            }
            // Right Eye
            Box(
                modifier = Modifier
                    .offset(x = 25.dp, y = (-5).dp)
                    .size(24.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(10.dp).shadow(6.dp, CircleShape, spotColor = Color(0xFF38BDF8)).background(Color(0xFF38BDF8), CircleShape))
            }
            
            // Snout
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-12).dp)
                    .size(width = 58.dp, height = 42.dp)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(30.dp)),
                contentAlignment = Alignment.TopCenter
            ) {
                // Nose
                Box(
                    modifier = Modifier
                        .offset(y = 7.dp)
                        .size(width = 20.dp, height = 12.dp)
                        .background(Color(0xFF0F172A), NoseShape())
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    
    val prefs = context.getSharedPreferences("PocketCashAuth", android.content.Context.MODE_PRIVATE)
    
    LaunchedEffect(Unit) {
        val savedEmail = prefs.getString("saved_email", "") ?: ""
        val savedPassword = prefs.getString("saved_password", "") ?: ""
        if (savedEmail.isNotEmpty() && savedPassword.isNotEmpty()) {
            email = savedEmail
            password = savedPassword
            rememberMe = true
        }
    }

    var referralCode by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(Country.INDIA) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08111F))
    ) {
        // Ambient Glow Effect Top Bulb
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-100).dp)
                .size(300.dp)
                .blur(100.dp)
                .background(Color(0xFF377DDD).copy(alpha = 0.2f), CircleShape)
        )
        // Ambient Glow Effect Bottom Bulb
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .size(250.dp)
                .blur(100.dp)
                .background(Color(0xFF155BC2).copy(alpha = 0.18f), CircleShape)
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Glassmorphism Card
                Column(
                    modifier = Modifier
                        .padding(top = 70.dp) // Space for husky
                        .fillMaxWidth()
                        .shadow(25.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.7f))
                        .background(Color(0xFF0F172A).copy(alpha = 0.7f), RoundedCornerShape(28.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(28.dp))
                        .padding(horizontal = 24.dp, vertical = 35.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    
                    // Toggle Mode Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                            .padding(4.dp)
                    ) {
                        TabButton(
                            text = "Sign In",
                            isActive = isLoginMode,
                            onClick = { isLoginMode = true },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            text = "Sign Up",
                            isActive = !isLoginMode,
                            onClick = { isLoginMode = false },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = if (isLoginMode) "Welcome Back" else "Create Account",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isLoginMode) "Enter your credentials to access your account" else "Register with your details to get started",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Form Fields
                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Email Address",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AuthTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Password",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    AnimatedVisibility(visible = !isLoginMode) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            AuthTextField(
                                value = referralCode,
                                onValueChange = { referralCode = it.uppercase() },
                                placeholder = "Referral Code (Optional)",
                                icon = Icons.Default.CardGiftcard,
                                keyboardType = KeyboardType.Text
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Country Selection Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(14.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                                    .padding(4.dp)
                            ) {
                                TabButton(
                                    text = "🇮🇳 India",
                                    isActive = selectedCountry == Country.INDIA,
                                    onClick = { selectedCountry = Country.INDIA },
                                    modifier = Modifier.weight(1f),
                                    activeColor = Color(0xFF38BDF8)
                                )
                                TabButton(
                                    text = "🇳🇵 Nepal",
                                    isActive = selectedCountry == Country.NEPAL,
                                    onClick = { selectedCountry = Country.NEPAL },
                                    modifier = Modifier.weight(1f),
                                    activeColor = Color(0xFF38BDF8)
                                )
                            }
                        }
                    }

                    if (isLoginMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 22.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = false, 
                                    onCheckedChange = {}, 
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF38BDF8), 
                                        uncheckedColor = Color(0xFF64748B)
                                    )
                                )
                                Text("Remember me", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            }
                            Text(
                                "Forgot Password?", 
                                fontSize = 12.sp, 
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.clickable { }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(22.dp))
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    if (isLoginMode) {
                                        viewModel.login(email, password)
                                    } else {
                                        val deviceId = android.provider.Settings.Secure.getString(context.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
                                        viewModel.signup(email, password, selectedCountry, referralCode, deviceId)
                                    }
                                    
                                    if (rememberMe) {
                                        prefs.edit()
                                            .putString("saved_email", email)
                                            .putString("saved_password", password)
                                            .apply()
                                    } else {
                                        prefs.edit().clear().apply()
                                    }
                                    onNavigateToHome()
                                } catch (e: Exception) {
                                    val msg = e.message ?: ""
                                    val errorMsg = when {
                                        msg.contains("credential is incorrect", ignoreCase = true) -> "Invalid email or password."
                                        msg.contains("invalid", ignoreCase = true) -> "Invalid email or password."
                                        msg.contains("already in use", ignoreCase = true) -> "This email is already registered."
                                        msg.contains("badly formatted", ignoreCase = true) -> "Invalid email format."
                                        else -> "Authentication failed. Please try again."
                                    }
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(10.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFF2563EB).copy(alpha = 0.4f)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading
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
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = if (isLoginMode) "Sign In" else "Create Account", 
                                    color = Color.White, 
                                    fontWeight = FontWeight.SemiBold, 
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row {
                        Text(
                            text = if (isLoginMode) "Don't have an account? " else "Already have an account? ",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isLoginMode) "Register" else "Sign In",
                            color = Color(0xFF38BDF8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { isLoginMode = !isLoginMode }
                        )
                    }
                }
                
                // Position Husky slightly overlapping the top
                Box(
                    modifier = Modifier.align(Alignment.TopCenter).offset(y = (-60).dp)
                ) {
                    AnimatedHusky()
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF2563EB)
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) Brush.linearGradient(listOf(activeColor, Color(0xFF0284C7))) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isActive) Color.White else Color(0xFF94A3B8),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFF94A3B8), fontSize = 14.sp) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(20.dp)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF38BDF8),
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
            focusedContainerColor = Color.White.copy(alpha = 0.07f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}
