import re

content = """package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
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
import com.example.viewmodel.MainViewModel

// Theming Colors
private val BgDark = Color(0xFF07090E)
private val ReferPrimary = Color(0xFF8B5CF6)
private val ReferSecondary = Color(0xFF38BDF8)
private val ReferGold = Color(0xFFFACC15)
private val GlassBg = Color.White.copy(alpha = 0.05f)
private val GlassBorder = Color.White.copy(alpha = 0.1f)
private val GlassBorderLight = Color.White.copy(alpha = 0.05f)
private val TextMuted = Color(0xFF94A3B8)
private val WhatsAppColor = Color(0xFF25D366)
private val TelegramColor = Color(0xFF0088cc)

@Composable
fun ReferScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val userState by viewModel.userState.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val referralStats by viewModel.referralStats.collectAsState()
    
    val refCode = userState.referralCode
    
    LaunchedEffect(refCode) {
        if (refCode.isNotEmpty()) {
            viewModel.fetchReferralStats(refCode)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // Ambient glows
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = (-40).dp)
                .size(260.dp)
                .blur(100.dp)
                .background(ReferPrimary.copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = (-100).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(ReferSecondary.copy(alpha = 0.25f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Screen Title
            Text(
                text = "Refer & Earn",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(start = 24.dp, top = 40.dp, bottom = 20.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            ) {
                // Hero Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = ReferPrimary.copy(alpha = 0.3f))
                            .background(Color(0xFF1E293B), RoundedCornerShape(24.dp))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(30.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(ReferGold.copy(alpha = 0.15f), CircleShape)
                                    .border(1.dp, ReferGold.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎁", fontSize = 34.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Invite Friends & Earn", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Get 50 Coins when your friend signs up and completes 10 Spins. They get 20 Coins!",
                                color = TextMuted, fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Referral Code Card
                item {
                    Text("YOUR UNIQUE CODE", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(ReferSecondary.copy(alpha = 0.1f))
                            .border(1.dp, ReferSecondary.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("SHARE CODE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                            Text(refCode.ifEmpty { "XXXXXX" }, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ReferSecondary.copy(alpha = 0.15f))
                                .border(1.dp, ReferSecondary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Code", refCode))
                                    Toast.makeText(context, "Code Copied!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = ReferSecondary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy", color = ReferSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Share Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { shareIntent(context, refCode, 50, "whatsapp") },
                            modifier = Modifier.weight(1f).height(50.dp).shadow(15.dp, RoundedCornerShape(16.dp), spotColor = WhatsAppColor.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        
                        Button(
                            onClick = { shareIntent(context, refCode, 50, "telegram") },
                            modifier = Modifier.weight(1f).height(50.dp).shadow(15.dp, RoundedCornerShape(16.dp), spotColor = TelegramColor.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.buttonColors(containerColor = TelegramColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Telegram", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                // Steps Section
                item {
                    Text("HOW IT WORKS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        StepCard("1", "Share Code", "Send your unique 6-character code.")
                        StepCard("2", "Friend Registers", "They enter your code when creating an account.")
                        StepCard("3", "Milestone Completed", "When they complete 10 spins, you both get rewarded!")
                    }
                }

                // Stats Section
                item {
                    Text("YOUR REFERRAL STATS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(GlassBg)
                            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${referralStats.first}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Successful Refers", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(GlassBorderLight))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${"%,d".format(referralStats.second)}", color = ReferGold, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Coins Earned", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepCard(num: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(GlassBg)
            .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(ReferPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .border(1.dp, ReferPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(num, color = ReferPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, color = TextMuted, fontSize = 11.sp)
        }
    }
}

fun shareIntent(context: Context, code: String, bonus: Int, packageTarget: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Join My App!")
        putExtra(
            Intent.EXTRA_TEXT,
            "Hey! Join me and use my referral code $code during signup. After you complete 10 spins, we both get bonus Coins! Download now."
        )
    }
    try {
        context.startActivity(Intent.createChooser(shareIntent, "Share via..."))
    } catch (e: Exception) {
        Toast.makeText(context, "App not found!", Toast.LENGTH_SHORT).show()
    }
}
"""

with open('app/src/main/java/com/example/ui/screens/ReferScreen.kt', 'w') as f:
    f.write(content)
