package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GeneratingTokens
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MainViewModel

@Composable
fun DailyRewardsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val userState by viewModel.userState.collectAsState()
    val canCheckIn = userState.canCheckIn
    val currentStreak = userState.currentStreak

    // Calculate display day (1 to 7)
    // If they can check in, active day is (streak % 7) + 1
    // If they can't check in, they already claimed today, so the day they claimed was ((streak - 1) % 7) + 1
    val activeDayIndex = if (canCheckIn) (currentStreak % 7) else -1
    val completedUpToIndex = if (canCheckIn) (currentStreak % 7) - 1 else ((currentStreak - 1) % 7)
    
    val displayDay = if (canCheckIn) (currentStreak % 7) + 1 else ((currentStreak - 1) % 7) + 1
    
    val rewards = listOf(10, 20, 30, 40, 50, 60, 200)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07090E))
    ) {
        // Ambient Background Glows
        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 30.dp)
                .size(220.dp)
                .blur(90.dp)
                .background(Color(0xFF7C3AED).copy(alpha = 0.3f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-30).dp, y = (-80).dp)
                .size(200.dp)
                .blur(90.dp)
                .background(Color(0xFF3B82F6).copy(alpha = 0.3f), CircleShape)
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
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Daily Gifts", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }

                Row(
                    modifier = Modifier
                        .background(Color(0xFFFACC15).copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFFFACC15).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${"%,d".format(userState.coinBalance)}",
                        color = Color(0xFFFACC15),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Streak Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161B26).copy(alpha = 0.65f), RoundedCornerShape(24.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Daily Streak", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("Roz app kholne par extra reward milega!", fontSize = 12.5.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(top = 2.dp))
                    }
                    Row(
                        modifier = Modifier
                            .background(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444))), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Day $displayDay", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Days 1-6 Grid
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (row in 0..1) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (col in 0..2) {
                                val dayIdx = row * 3 + col
                                val isClaimed = dayIdx <= completedUpToIndex
                                val isActive = dayIdx == activeDayIndex

                                val bgColor = if (isActive) Brush.linearGradient(listOf(Color(0xFF8B5CF6).copy(alpha=0.25f), Color(0xFF1E293B).copy(alpha=0.8f)))
                                              else Brush.linearGradient(listOf(Color(0xFF161B26).copy(alpha = 0.65f), Color(0xFF161B26).copy(alpha = 0.65f)))
                                
                                val borderColor = if (isActive) Color(0xFF8B5CF6)
                                                  else if (isClaimed) Color(0xFF22C55E).copy(alpha = 0.4f)
                                                  else Color.White.copy(alpha = 0.12f)
                                                  
                                val alpha = if (isClaimed && !isActive) 0.7f else 1f

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(bgColor, RoundedCornerShape(20.dp))
                                        .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                                        .padding(vertical = 16.dp, horizontal = 10.dp)
                                        .blur(if (isClaimed && !isActive) 0.dp else 0.dp), // You can optionally blur claimed items slightly
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Day ${dayIdx + 1}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), modifier = Modifier.padding(bottom = 8.dp))
                                    
                                    if (isClaimed) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(24.dp).padding(bottom = 4.dp))
                                    } else {
                                        Icon(Icons.Default.GeneratingTokens, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(24.dp).padding(bottom = 4.dp))
                                    }
                                    
                                    Text("+${rewards[dayIdx]}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFACC15))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Day 7 Super Reward
                val isDay7Claimed = completedUpToIndex == 6
                val isDay7Active = activeDayIndex == 6
                
                val d7Bg = if (isDay7Active) Brush.linearGradient(listOf(Color(0xFFFACC15).copy(alpha = 0.25f), Color(0xFF7C3AED).copy(alpha = 0.4f)))
                           else Brush.linearGradient(listOf(Color(0xFFFACC15).copy(alpha = 0.2f), Color(0xFF7C3AED).copy(alpha = 0.3f)))
                           
                val d7Border = if (isDay7Active) Color(0xFFFACC15)
                               else if (isDay7Claimed) Color(0xFF22C55E).copy(alpha = 0.4f)
                               else Color(0xFFFACC15).copy(alpha = 0.4f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(d7Bg, RoundedCornerShape(20.dp))
                        .border(1.dp, d7Border, RoundedCornerShape(20.dp))
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Day 7 Super Reward", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFACC15))
                        Text("+${rewards[6]} Coins", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(top = 2.dp))
                    }
                    if (isDay7Claimed) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(32.dp))
                    } else {
                        Icon(Icons.Default.Redeem, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Claim Button
                Button(
                    onClick = {
                        if (canCheckIn) {
                            viewModel.performDailyCheckIn()
                        }
                    },
                    enabled = canCheckIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (canCheckIn) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))),
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Redeem, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Claim Day ${activeDayIndex + 1} (+${rewards[activeDayIndex]} Coins)", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Already Claimed Today", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }
        }
    }
}
