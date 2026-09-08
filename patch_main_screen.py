import re

content = """package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun MainAppScreen(
    mainViewModel: MainViewModel,
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBackground)
    ) {
        // Cosmic Background Auras
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = (-50).dp)
                .size(250.dp)
                .blur(100.dp)
                .background(PremiumPrimary.copy(alpha = 0.2f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = (-150).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(MovieAccent.copy(alpha = 0.2f), CircleShape)
        )

        // Main Content
        NavHost(
            navController = bottomNavController,
            startDestination = "home_tab",
            modifier = Modifier
                .fillMaxSize()
                // Leave space for floating bottom nav
                .padding(bottom = 90.dp)
        ) {
            composable("home_tab") {
                HomeScreen(
                    viewModel = mainViewModel,
                    onNavigateToTasks = { rootNavController.navigate("tasks") },
                    onNavigateToWallet = { bottomNavController.navigate("withdraw_tab") },
                    onNavigateToDeals = { rootNavController.navigate("deals") },
                    onNavigateToProfile = { bottomNavController.navigate("profile_tab") },
                    onNavigateToSpin = { rootNavController.navigate("spin") },
                    onNavigateToScratch = { rootNavController.navigate("scratch") },
                    onNavigateToQuiz = { rootNavController.navigate("quiz") },
                    onNavigateToDailyRewards = { rootNavController.navigate("daily_rewards") },
                    onNavigateToVideo = { rootNavController.navigate("watch_video") },
                    onNavigateToRefer = { bottomNavController.navigate("refer_tab") },
                    onNavigateToLeaderboard = { bottomNavController.navigate("leaderboard_tab") }
                )
            }
            composable("withdraw_tab") {
                WalletScreen(
                    viewModel = mainViewModel,
                    onBack = { bottomNavController.popBackStack() },
                    onNavigateToHistory = { rootNavController.navigate("history") }
                )
            }
            composable("leaderboard_tab") {
                LeaderboardScreen(viewModel = mainViewModel)
            }
            composable("refer_tab") {
                ReferScreen(viewModel = mainViewModel)
            }
            composable("profile_tab") {
                ProfileScreen(
                    viewModel = mainViewModel,
                    onBack = { bottomNavController.popBackStack() },
                    onNavigateToHistory = { rootNavController.navigate("history") },
                    onNavigateToAdmin = { rootNavController.navigate("admin_panel") },
                    onLogout = {
                        rootNavController.navigate("auth") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Floating Bottom Navigation
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(70.dp)
                    .shadow(30.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.6f))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xE60B0E14)) // rgba(11, 14, 20, 0.9)
                    .border(1.dp, PremiumOutlineVariant, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                CustomBottomNavItem(
                    label = "Home",
                    icon = Icons.Default.Home,
                    selected = currentRoute == "home_tab",
                    onClick = { bottomNavController.navigate("home_tab") { popUpTo(bottomNavController.graph.startDestinationId) { saveState = true } ; launchSingleTop = true; restoreState = true } }
                )
                CustomBottomNavItem(
                    label = "Withdraw",
                    icon = Icons.Default.AccountBalanceWallet,
                    selected = currentRoute == "withdraw_tab",
                    onClick = { bottomNavController.navigate("withdraw_tab") { popUpTo(bottomNavController.graph.startDestinationId) { saveState = true } ; launchSingleTop = true; restoreState = true } }
                )
                CustomBottomNavItem(
                    label = "Top 50",
                    icon = Icons.Default.EmojiEvents,
                    selected = currentRoute == "leaderboard_tab",
                    onClick = { bottomNavController.navigate("leaderboard_tab") { popUpTo(bottomNavController.graph.startDestinationId) { saveState = true } ; launchSingleTop = true; restoreState = true } }
                )
                CustomBottomNavItem(
                    label = "Refer",
                    icon = Icons.Default.Share,
                    selected = currentRoute == "refer_tab",
                    onClick = { bottomNavController.navigate("refer_tab") { popUpTo(bottomNavController.graph.startDestinationId) { saveState = true } ; launchSingleTop = true; restoreState = true } }
                )
                CustomBottomNavItem(
                    label = "Profile",
                    icon = Icons.Default.Person,
                    selected = currentRoute == "profile_tab",
                    onClick = { bottomNavController.navigate("profile_tab") { popUpTo(bottomNavController.graph.startDestinationId) { saveState = true } ; launchSingleTop = true; restoreState = true } }
                )
            }
        }
    }
}

@Composable
fun CustomBottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null, // No ripple for custom scale effect
                onClick = onClick
            )
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color.White else PremiumOnSurfaceVariant,
            modifier = Modifier
                .size(22.dp)
                .offset(y = if (selected) (-3).dp else 0.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (selected) PremiumSecondary else PremiumOnSurfaceVariant,
            maxLines = 1
        )
        if (selected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(2.dp)
                    .shadow(10.dp, CircleShape, spotColor = PremiumSecondary)
                    .background(PremiumSecondary, CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}
"""

with open('app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
