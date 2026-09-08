package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.viewmodel.MainViewModel

// Colors
private val HomeBgDeep = Color(0xFF030712)
private val HomeNeonCyan = Color(0xFF00F0FF)
private val HomeNeonPink = Color(0xFFFF007F)
private val HomeNeonPurple = Color(0xFF7000FF)
private val HomeNeonGold = Color(0xFFFFB700)
private val HomeNeonGreen = Color(0xFF00FF88)
private val HomeGlassCard = Color(0xFF0F172A).copy(alpha = 0.65f)
private val HomeGlassBorder = Color.White.copy(alpha = 0.12f)
private val HomeTextMain = Color(0xFFF8FAFC)
private val HomeTextSub = Color(0xFF94A3B8)

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToDeals: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSpin: () -> Unit,
    onNavigateToScratch: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToDailyRewards: () -> Unit,
    onNavigateToVideo: () -> Unit,
    onNavigateToRefer: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToMathEarn: () -> Unit,
    onNavigateToCardMatch: () -> Unit
) {
    val userState by viewModel.userState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBgDeep)
    ) {
        // Ambient Glowing Orbs
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = (-40).dp)
                .size(220.dp)
                .blur(80.dp)
                .background(HomeNeonPurple.copy(alpha = 0.45f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = (-100).dp)
                .size(180.dp)
                .blur(80.dp)
                .background(HomeNeonPink.copy(alpha = 0.45f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-40).dp, y = 40.dp)
                .size(150.dp)
                .blur(80.dp)
                .background(HomeNeonCyan.copy(alpha = 0.45f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF030712).copy(alpha = 0.6f))
                    .padding(vertical = 18.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onNavigateToProfile() }) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Brush.linearGradient(listOf(HomeNeonCyan, HomeNeonPurple)), RoundedCornerShape(14.dp))
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = "https://api.dicebear.com/7.x/bottts/png?seed=${userState?.displayName ?: "Sanjay"}",
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF090D16), RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = userState?.displayName ?: "PocketCash Pro",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HomeTextMain
                        )
                        Text(
                            text = "● ONLINE PRO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HomeNeonGreen
                        )
                    }
                }
                
                Row {
                    IconButton(
                        onClick = onNavigateToLeaderboard,
                        modifier = Modifier
                            .size(40.dp)
                            .background(HomeGlassCard, RoundedCornerShape(14.dp))
                            .border(1.dp, HomeGlassBorder, RoundedCornerShape(14.dp))
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Trophy", tint = HomeTextMain, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier
                            .size(40.dp)
                            .background(HomeGlassCard, RoundedCornerShape(14.dp))
                            .border(1.dp, HomeGlassBorder, RoundedCornerShape(14.dp))
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = "Flash", tint = HomeTextMain, modifier = Modifier.size(20.dp))
                    }
                }
            }
            
            // Main Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp)
            ) {
                // Hero Wallet Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 25.dp)
                            .shadow(40.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.5f))
                            .clip(RoundedCornerShape(28.dp))
                            .background(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))))
                            .border(1.dp, HomeGlassBorder, RoundedCornerShape(28.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .graphicsLayer(rotationZ = rotation)
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(HomeNeonCyan.copy(alpha = 0.15f), Color.Transparent),
                                            radius = size.width * 0.6f
                                        )
                                    )
                                }
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("CURRENT BALANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp, color = HomeTextSub)
                                Box(
                                    modifier = Modifier
                                        .background(HomeNeonGreen.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                        .border(1.dp, HomeNeonGreen, RoundedCornerShape(20.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("INSTANT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HomeNeonGreen)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(verticalAlignment = Alignment.Bottom) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = "Coin", tint = HomeNeonGold, modifier = Modifier.size(24.dp).padding(bottom = 4.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${userState?.coinBalance ?: 0}", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = HomeTextMain)
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Button(
                                onClick = onNavigateToWallet,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.horizontalGradient(listOf(HomeNeonCyan, HomeNeonPurple)), RoundedCornerShape(18.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("WITHDRAW FUNDS", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Go", tint = Color.Black, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Featured Tasks Heading
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("FEATURED TASKS", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = HomeTextSub)
                    }
                }

                // Grid (2 columns)
                item {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            ActionCard(
                                title = "Daily Gift",
                                icon = Icons.Default.CardGiftcard,
                                iconColor = HomeNeonGold,
                                bgColor = HomeNeonGold.copy(alpha = 0.12f),
                                onClick = onNavigateToDailyRewards,
                                modifier = Modifier.weight(1f)
                            )
                            ActionCard(
                                title = "Spin Wheel",
                                icon = Icons.Default.Refresh,
                                iconColor = HomeNeonCyan,
                                bgColor = HomeNeonCyan.copy(alpha = 0.12f),
                                onClick = onNavigateToSpin,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            ActionCard(
                                title = "Scratch",
                                icon = Icons.Default.ConfirmationNumber,
                                iconColor = HomeNeonGreen,
                                bgColor = HomeNeonGreen.copy(alpha = 0.12f),
                                onClick = onNavigateToScratch,
                                modifier = Modifier.weight(1f)
                            )
                            ActionCard(
                                title = "Quiz Blitz",
                                icon = Icons.Default.Psychology,
                                iconColor = HomeNeonPink,
                                bgColor = HomeNeonPink.copy(alpha = 0.12f),
                                onClick = onNavigateToQuiz,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 28.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            ActionCard(
                                title = "Math Blitz",
                                icon = Icons.Default.Calculate,
                                iconColor = HomeNeonPurple,
                                bgColor = HomeNeonPurple.copy(alpha = 0.12f),
                                onClick = onNavigateToMathEarn,
                                modifier = Modifier.weight(1f)
                            )
                            ActionCard(
                                title = "Card Match",
                                icon = Icons.Default.Style,
                                iconColor = HomeNeonCyan,
                                bgColor = HomeNeonCyan.copy(alpha = 0.12f),
                                onClick = onNavigateToCardMatch,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Premium Earnings Heading
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PREMIUM EARNINGS", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = HomeTextSub)
                    }
                }

                // Task Stack
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TaskRow(
                            title = "Watch & Earn",
                            subtitle = "Watch short video ads to earn +100 Coins",
                            icon = Icons.Default.PlayArrow,
                            iconColor = HomeNeonPink,
                            bgColor = HomeNeonPink.copy(alpha = 0.12f),
                            onClick = onNavigateToVideo
                        )
                        TaskRow(
                            title = "Solve Captcha",
                            subtitle = "Instant rewards on puzzle solves",
                            icon = Icons.Default.Extension,
                            iconColor = HomeNeonCyan,
                            bgColor = HomeNeonCyan.copy(alpha = 0.12f),
                            onClick = onNavigateToTasks
                        )
                        TaskRow(
                            title = "Shopping Cashbacks",
                            subtitle = "Get up to 15% instant cashback",
                            icon = Icons.Default.ShoppingBag,
                            iconColor = HomeNeonGold,
                            bgColor = HomeNeonGold.copy(alpha = 0.12f),
                            onClick = onNavigateToDeals
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(HomeGlassCard, RoundedCornerShape(22.dp))
            .border(1.dp, HomeGlassBorder, RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .padding(vertical = 20.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(bgColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HomeTextMain)
        }
    }
}

@Composable
fun TaskRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HomeGlassCard, RoundedCornerShape(20.dp))
            .border(1.dp, HomeGlassBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(bgColor, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HomeTextMain)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 12.sp, color = HomeTextSub)
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Go", tint = HomeTextSub, modifier = Modifier.size(12.dp))
        }
    }
}

@Composable
fun IconButtonGlass(icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(Color(0xFF0F172A).copy(alpha = 0.65f), RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}
