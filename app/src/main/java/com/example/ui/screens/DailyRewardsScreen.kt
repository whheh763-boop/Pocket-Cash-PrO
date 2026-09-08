package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyRewardsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val userState by viewModel.userState.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val context = LocalContext.current

    val tier = when {
        userState.totalCheckIns >= 30 -> "Gold"
        userState.totalCheckIns >= 7 -> "Silver"
        else -> "Bronze"
    }
    
    val tierColor = when (tier) {
        "Gold" -> Color(0xFFFFD700)
        "Silver" -> Color(0xFFC0C0C0)
        else -> Color(0xFFCD7F32)
    }

    Scaffold(
        containerColor = com.example.ui.theme.PremiumBackground,
        topBar = {
            TopAppBar(
                title = { Text("Daily Rewards") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = com.example.ui.theme.PremiumBackground,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tier Badge
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(tierColor.copy(alpha = 0.2f), CircleShape)
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(tierColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("$tier Member", color = tierColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Keep checking in to upgrade your tier!", color = Color(0xFFA0ABC0), fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // Stats Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Current Streak",
                    value = "${userState.currentStreak} Days",
                    color = Color(0xFF10B981)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Check-ins",
                    value = "${userState.totalCheckIns}",
                    color = Color(0xFF3B82F6)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E212A))
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Today's Reward", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("+${appConfig.dailyCheckInReward} Coins", color = Color(0xFFF59E0B), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            viewModel.performDailyCheckIn()
                            // Play sound
                            try {
                                val notification = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                                val r = android.media.RingtoneManager.getRingtone(context, notification)
                                r.play()
                                Toast.makeText(context, "Claimed successfully!", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        enabled = userState.canCheckIn,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        if (userState.canCheckIn) {
                            Text("Claim Reward", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Claimed Today", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, color: Color) {
    Column(
        modifier = modifier
            .background(Color(0xFF1E212A), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = Color(0xFFA0ABC0), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
