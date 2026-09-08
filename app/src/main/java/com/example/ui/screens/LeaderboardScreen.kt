package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.User
import com.example.viewmodel.MainViewModel
import com.example.ads.BannerAdView

// Colors based on HTML
private val LeaderboardBgDark = Color(0xFF030407)
private val LeaderboardGlassBg = Color(0xFF161B26).copy(alpha = 0.55f)
private val LeaderboardGlassBorder = Color.White.copy(alpha = 0.08f)
private val LeaderboardPrimary = Color(0xFF6366F1)
private val LeaderboardSecondary = Color(0xFF22D3EE)
private val LeaderboardGold = Color(0xFFFACC15)
private val LeaderboardSilver = Color(0xFF94A3B8)
private val LeaderboardBronze = Color(0xFFD97706)
private val LeaderboardTextMuted = Color(0xFF94A3B8)

@Composable
fun LeaderboardScreen(viewModel: MainViewModel) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    val currentUser by viewModel.userState.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    
    // Sort just in case, though viewmodel should handle it
    val sortedLeaderboard = leaderboard.sortedByDescending { it.coinBalance }
    val top3 = sortedLeaderboard.take(3)
    val remaining = sortedLeaderboard.drop(3)
    
    // Calculate current user's rank
    val currentUserRank = sortedLeaderboard.indexOfFirst { it.uid == currentUser.uid } + 1
    val coinsToTop10 = if (currentUserRank > 10) {
        val rank10Coins = sortedLeaderboard.getOrNull(9)?.coinBalance ?: 0
        (rank10Coins - currentUser.coinBalance).coerceAtLeast(0)
    } else 0
    
    Box(modifier = Modifier.fillMaxSize().background(LeaderboardBgDark)) {
        // Auras
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-50).dp, y = (-50).dp)
                .size(260.dp)
                .blur(100.dp)
                .background(LeaderboardGold.copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-50).dp, y = (-100).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(LeaderboardPrimary.copy(alpha = 0.25f), CircleShape)
        )
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 22.dp)
                    .padding(bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Top 50 Earners", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Icon(Icons.Default.EmojiEvents, contentDescription = "Trophy", tint = LeaderboardGold, modifier = Modifier.size(22.dp))
            }
            
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp)
                    .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
                    .border(1.dp, LeaderboardGlassBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                val tabs = listOf("Today", "This Week", "All Time")
                tabs.forEachIndexed { index, title ->
                    val isActive = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isActive) Brush.linearGradient(listOf(LeaderboardPrimary, LeaderboardSecondary))
                                else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                            )
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            title,
                            color = if (isActive) Color.Black else LeaderboardTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Ad Banner
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), contentAlignment = Alignment.Center) {
                BannerAdView()
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 100.dp)
            ) {
                if (top3.isNotEmpty()) {
                    item {
                        // Podium Container
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Rank 2
                            if (top3.size >= 2) {
                                Box(modifier = Modifier.weight(1f)) {
                                    PodiumCard(user = top3[1], rank = 2)
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            
                            // Rank 1
                            Box(modifier = Modifier.weight(1f).offset(y = (-10).dp)) {
                                PodiumCard(user = top3[0], rank = 1)
                            }
                            
                            // Rank 3
                            if (top3.size >= 3) {
                                Box(modifier = Modifier.weight(1f)) {
                                    PodiumCard(user = top3[2], rank = 3)
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                
                if (remaining.isNotEmpty()) {
                    item {
                        Text(
                            "LEADERBOARD RANKINGS", 
                            color = LeaderboardTextMuted, 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            letterSpacing = 1.sp, 
                            modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                        )
                    }
                    
                    itemsIndexed(remaining) { index, user ->
                        val actualRank = index + 4
                        LeaderboardListItem(user = user, rank = actualRank)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
        
        // Sticky My Rank Footer
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(30.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.5f))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(LeaderboardPrimary.copy(alpha = 0.3f), LeaderboardSecondary.copy(alpha = 0.15f))))
                    .border(1.dp, LeaderboardSecondary, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "#${if (currentUserRank > 0) currentUserRank else "--"}",
                        color = LeaderboardSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(30.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Your Current Rank", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        if (currentUserRank > 10 && currentUserRank > 0) {
                            Text("${coinsToTop10} coins to reach Top 10", color = LeaderboardTextMuted, fontSize = 11.sp)
                        } else if (currentUserRank in 1..10) {
                            Text("You are in the Top 10!", color = LeaderboardGold, fontSize = 11.sp)
                        } else {
                            Text("Play and earn more coins!", color = LeaderboardTextMuted, fontSize = 11.sp)
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = LeaderboardGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${currentUser.coinBalance}", color = LeaderboardGold, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun PodiumCard(user: User, rank: Int) {
    val borderColor = when (rank) {
        1 -> LeaderboardGold.copy(alpha = 0.4f)
        else -> LeaderboardGlassBorder
    }
    val bgColor = when (rank) {
        1 -> Brush.verticalGradient(listOf(LeaderboardGold.copy(alpha = 0.1f), LeaderboardGlassBg))
        else -> Brush.verticalGradient(listOf(LeaderboardGlassBg, LeaderboardGlassBg))
    }
    
    val badgeColor = when (rank) {
        1 -> LeaderboardGold
        2 -> LeaderboardSilver
        else -> LeaderboardBronze
    }
    
    val avatarSize = if (rank == 1) 62.dp else 50.dp
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(vertical = 14.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.padding(bottom = 8.dp), contentAlignment = Alignment.TopCenter) {
            AsyncImage(
                model = "https://i.pravatar.cc/100?img=${10 + rank}",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(2.dp, if (rank == 1) LeaderboardGold else LeaderboardTextMuted, CircleShape)
            )
            Icon(
                Icons.Default.WorkspacePremium,
                contentDescription = "Badge",
                tint = badgeColor,
                modifier = Modifier
                    .size(if (rank == 1) 24.dp else 20.dp)
                    .offset(y = (-4).dp)
            )
        }
        
        val displayName = if (user.email.isNotEmpty()) {
            user.email.substringBefore("@").take(8)
        } else {
            "User${user.uid.take(4)}"
        }
        
        Text(
            displayName,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = LeaderboardGold, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                "${user.coinBalance}",
                color = LeaderboardGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun LeaderboardListItem(user: User, rank: Int) {
    val displayName = if (user.email.isNotEmpty()) {
        user.email.substringBefore("@").take(12)
    } else {
        "User${user.uid.take(6)}"
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(LeaderboardGlassBg)
            .border(1.dp, LeaderboardGlassBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "$rank",
                color = LeaderboardTextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.width(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                val initial = displayName.firstOrNull()?.uppercase() ?: "U"
                Text(initial, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(displayName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Completed Offers", color = LeaderboardTextMuted, fontSize = 11.sp)
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = LeaderboardGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("${user.coinBalance}", color = LeaderboardGold, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
