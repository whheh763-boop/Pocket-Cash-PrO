import re

content = """package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.BannerAdView
import com.example.viewmodel.MainViewModel
import com.example.ui.theme.*
import androidx.compose.ui.graphics.SolidColor

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToDeals: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSpin: () -> Unit,
    onNavigateToScratch: () -> Unit,
    onNavigateToVideo: () -> Unit,
    onNavigateToRefer: () -> Unit,
    onNavigateToLeaderboard: () -> Unit
) {
    val userState by viewModel.userState.collectAsState()
    
    // Animation states
    var showHeader by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(false) }
    var showTasks by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        showHeader = true
        kotlinx.coroutines.delay(100)
        showGrid = true
        kotlinx.coroutines.delay(100)
        showTasks = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0E15),
                        Color(0xFF13111C),
                        Color(0xFF0A0B10)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // CUSTOM TOP BAR
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo & Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(GradientLogoStart, GradientLogoEnd)))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Row {
                            Text("PocketCash", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                            Text(" Pro", color = PremiumPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        }
                    }
                    
                    // Actions
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(
                            onClick = onNavigateToLeaderboard,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF151624), CircleShape)
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = "Trophy", tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF151624), CircleShape)
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // TOTAL BALANCE CARD
            item {
                AnimatedVisibility(visible = showHeader, enter = fadeIn() + slideInVertically(initialOffsetY = { -50 })) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = PremiumPrimary.copy(alpha = 0.2f))
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1C1C28))
                            .border(1.dp, Color(0xFF2D2E40), RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Column {
                            Text("TOTAL BALANCE", color = Color(0xFFA0ABC0), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Glowing Coin Box
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .shadow(16.dp, CircleShape, spotColor = PremiumSecondary, ambientColor = PremiumSecondary)
                                            .background(PremiumSecondary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Toll, contentDescription = "Coin", tint = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        "${userState.coinBalance}",
                                        color = Color.White,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                
                                // Withdraw Button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Brush.horizontalGradient(listOf(GradientButtonStart, GradientButtonEnd)))
                                        .clickable { onNavigateToWallet() }
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Withdraw", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // GRID ACTIONS (Daily, Spin, Scratch)
            item {
                AnimatedVisibility(visible = showGrid, enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GridActionCardPremium(
                            modifier = Modifier.weight(1f),
                            title = "Daily Gift",
                            icon = Icons.Default.CardGiftcard,
                            iconTint = IconTintYellow,
                            onClick = { viewModel.performDailyCheckIn() },
                            enabled = userState.canCheckIn
                        )
                        GridActionCardPremium(
                            modifier = Modifier.weight(1f),
                            title = "Spin Wheel",
                            icon = Icons.Default.Star,
                            iconTint = IconTintCyan,
                            onClick = { onNavigateToSpin() }
                        )
                        GridActionCardPremium(
                            modifier = Modifier.weight(1f),
                            title = "Scratch",
                            icon = Icons.Default.CheckCircle,
                            iconTint = IconTintGreen,
                            onClick = { onNavigateToScratch() }
                        )
                    }
                }
            }
            
            // PREMIUM TASKS
            item {
                AnimatedVisibility(visible = showTasks, enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("PREMIUM TASKS", color = Color(0xFFA0ABC0), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("See All", color = IconTintCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onNavigateToTasks() })
                        }
                        
                        TaskCardPremium(
                            title = "Watch & Earn",
                            subtitle = "Earn coins by watching ads & videos",
                            icon = Icons.Default.Videocam,
                            iconTint = IconTintRed,
                            onClick = { onNavigateToVideo() }
                        )
                        TaskCardPremium(
                            title = "Math & Captcha Solves",
                            subtitle = "Solve quick puzzles for instant coins",
                            icon = Icons.Default.Extension,
                            iconTint = IconTintBlue,
                            onClick = { onNavigateToTasks() }
                        )
                        TaskCardPremium(
                            title = "Shopping Deals & Cashback",
                            subtitle = "Get cashback on purchases",
                            icon = Icons.Default.LocalMall,
                            iconTint = IconTintPink,
                            onClick = { onNavigateToDeals() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GridActionCardPremium(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF13141F))
            .border(1.dp, Color(0xFF222333), RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconTint.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Text(
                title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

@Composable
fun TaskCardPremium(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF13141F))
            .border(1.dp, Color(0xFF222333), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color(0xFF1E1F30), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(26.dp))
            }
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                Text(subtitle, fontSize = 12.sp, color = Color(0xFFA0ABC0))
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFA0ABC0)
        )
    }
}
"""

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'w') as f:
    f.write(content)

