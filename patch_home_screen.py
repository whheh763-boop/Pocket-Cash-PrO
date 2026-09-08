import re

content = """package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

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
    onNavigateToLeaderboard: () -> Unit
) {
    val userState by viewModel.userState.collectAsState()
    var showTasks by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showTasks = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent), // Let MainAppScreen handle background
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(25.dp, RoundedCornerShape(16.dp), spotColor = PremiumPrimary)
                            .background(Brush.linearGradient(listOf(GradientMainStart, GradientMainEnd)), RoundedCornerShape(16.dp))
                            .border(1.dp, PremiumOutline, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Text(
                        buildAnnotatedString {
                            append("PocketCash ")
                            withStyle(SpanStyle(color = PremiumPrimary)) {
                                append("Pro")
                            }
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.6).sp,
                        color = Color.White
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButtonGlass(icon = Icons.Default.EmojiEvents, onClick = onNavigateToLeaderboard)
                    IconButtonGlass(icon = Icons.Default.Notifications, onClick = {})
                }
            }
        }

        item {
            // Total Balance Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 28.dp)
                    .shadow(40.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                    .clip(RoundedCornerShape(28.dp))
                    .background(PremiumSurface)
                    .border(1.dp, PremiumOutlineVariant, RoundedCornerShape(28.dp))
                    .padding(26.dp)
            ) {
                // Aura inside card
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = (-30).dp, y = (-30).dp)
                        .blur(50.dp)
                        .background(PremiumPrimary.copy(alpha = 0.18f), CircleShape)
                )
                
                Column {
                    Text(
                        text = "TOTAL BALANCE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PremiumOnSurfaceVariant,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .shadow(20.dp, CircleShape, spotColor = IconTintYellow.copy(alpha = 0.6f))
                                    .background(Brush.linearGradient(listOf(GradientSecondaryStart, GradientSecondaryEnd)), CircleShape)
                                    .border(1.dp, PremiumOutline, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Toll, contentDescription = "Coins", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Text(
                                text = "${userState.balance}",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        
                        // Withdraw Button
                        Row(
                            modifier = Modifier
                                .shadow(25.dp, RoundedCornerShape(18.dp), spotColor = PremiumPrimary.copy(alpha = 0.5f))
                                .background(Brush.linearGradient(listOf(GradientMainStart, GradientMainEnd)), RoundedCornerShape(18.dp))
                                .clickable { onNavigateToWallet() }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Withdraw", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        item {
            // Quick Actions Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Daily Gift",
                    icon = Icons.Default.CardGiftcard,
                    iconTint = IconTintYellow,
                    bgColor = IconTintYellow.copy(alpha = 0.1f),
                    onClick = onNavigateToDailyRewards
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Spin Wheel",
                    icon = Icons.Default.DataUsage,
                    iconTint = PremiumSecondary,
                    bgColor = PremiumSecondary.copy(alpha = 0.1f),
                    onClick = onNavigateToSpin
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Scratch",
                    icon = Icons.Default.LocalActivity,
                    iconTint = IconTintGreen,
                    bgColor = IconTintGreen.copy(alpha = 0.1f),
                    onClick = onNavigateToScratch
                )
            }
        }

        item {
            // Premium Tasks
            AnimatedVisibility(visible = showTasks, enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PREMIUM TASKS", color = PremiumOnSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
                        Text("See All", color = PremiumSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onNavigateToTasks() })
                    }
                    
                    TaskCardPremiumGlass(
                        title = "Watch & Earn",
                        subtitle = "Earn coins by watching ads & videos",
                        icon = Icons.Default.Videocam,
                        iconTint = IconTintRed,
                        onClick = onNavigateToVideo
                    )
                    TaskCardPremiumGlass(
                        title = "Math & Captcha Solves",
                        subtitle = "Solve quick puzzles for instant coins",
                        icon = Icons.Default.Extension,
                        iconTint = IconTintBlue,
                        onClick = onNavigateToTasks
                    )
                    TaskCardPremiumGlass(
                        title = "Shopping Deals & Cashback",
                        subtitle = "Get cashback on purchases",
                        icon = Icons.Default.LocalMall,
                        iconTint = IconTintPink,
                        onClick = onNavigateToDeals
                    )
                }
            }
        }
    }
}

@Composable
fun IconButtonGlass(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(PremiumSurface)
            .border(1.dp, PremiumOutline, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = PremiumOnSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    iconTint: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .shadow(15.dp, RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.15f))
            .clip(RoundedCornerShape(22.dp))
            .background(PremiumSurface)
            .border(1.dp, PremiumOutline, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(bgColor, RoundedCornerShape(16.dp))
                .border(1.dp, PremiumOutline.copy(alpha = 0.06f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE2E8F0), maxLines = 1)
    }
}

@Composable
fun TaskCardPremiumGlass(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(PremiumSurface)
            .border(1.dp, PremiumOutline, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .border(1.dp, PremiumOutline, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp, modifier = Modifier.padding(bottom = 4.dp))
                Text(subtitle, fontSize = 12.sp, color = PremiumOnSurfaceVariant, fontWeight = FontWeight.Normal)
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = PremiumOnSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
"""

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'w') as f:
    f.write(content)
