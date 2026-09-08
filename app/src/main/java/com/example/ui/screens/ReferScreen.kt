package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MainViewModel

// Brand Colors from Design
val ReferPrimary = Color(0xFF6366F1)
val ReferSecondary = Color(0xFF22D3EE)
val ReferGold = Color(0xFFFACC15)
val WhatsAppColor = Color(0xFF25D366)
val TelegramColor = Color(0xFF0088CC)
val TextMuted = Color(0xFF94A3B8)
val GlassBg = Color(0xFF161B26).copy(alpha = 0.55f)
val GlassBorder = Color.White.copy(alpha = 0.08f)
val GlassBorderLight = Color.White.copy(alpha = 0.15f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferScreen(viewModel: MainViewModel, onBack: () -> Unit = {}) {
    val userState by viewModel.userState.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val context = LocalContext.current
    
    val refCode = userState.referralCode
    var applyCodeText by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030407))
    ) {
        // Ambient Background Auras
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .size(250.dp)
                .blur(100.dp)
                .background(ReferPrimary.copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = (-100).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(ReferSecondary.copy(alpha = 0.25f), CircleShape)
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
                Text("Refer & Earn", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Icon(Icons.Default.Info, contentDescription = "Info", tint = TextMuted, modifier = Modifier.size(22.dp).clickable { })
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 10.dp, bottom = 100.dp)
            ) {
                
                // Referral Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Brush.linearGradient(listOf(ReferPrimary.copy(alpha = 0.2f), ReferSecondary.copy(alpha = 0.08f))))
                            .border(1.dp, GlassBorderLight, RoundedCornerShape(28.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .shadow(25.dp, RoundedCornerShape(20.dp), spotColor = ReferPrimary.copy(alpha = 0.8f))
                                    .background(Brush.linearGradient(listOf(Color(0xFFA855F7), ReferPrimary)), RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Invite Friends & Earn", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Earn ", color = TextMuted, fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "${appConfig.referralBonus} Coins", color = ReferGold, fontSize = 14.sp, fontWeight = FontWeight.Bold
                            )
                            Text(
                                " for every friend who joins & completes 1st task!", color = TextMuted, fontSize = 13.sp, textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Apply Referral Code Card
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(GlassBg)
                            .border(1.dp, GlassBorderLight, RoundedCornerShape(22.dp))
                            .padding(16.dp)
                    ) {
                        Text("HAVE A REFERRAL CODE?", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = applyCodeText,
                                onValueChange = { applyCodeText = it.uppercase() },
                                placeholder = { Text("Enter Code Here", color = TextMuted, fontSize = 14.sp) },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = GlassBorder,
                                    focusedBorderColor = ReferSecondary,
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Button(
                                onClick = { 
                                    if(applyCodeText.isBlank()){
                                        Toast.makeText(context, "Please enter a referral code first!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Referral Code '${applyCodeText}' Applied Successfully! 🎉", Toast.LENGTH_SHORT).show()
                                        applyCodeText = ""
                                    }
                                },
                                modifier = Modifier.height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .background(Brush.linearGradient(listOf(ReferSecondary, ReferPrimary)), RoundedCornerShape(14.dp))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("APPLY", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                // Your Referral Code Section
                item {
                    Text("YOUR REFERRAL CODE", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(GlassBg)
                            .border(1.dp, ReferSecondary.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("SHARE CODE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                            Text(refCode.ifEmpty { "POCKET500" }, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ReferSecondary.copy(alpha = 0.15f))
                                .border(1.dp, ReferSecondary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Code", refCode.ifEmpty { "POCKET500" }))
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
                            onClick = { shareIntent(context, refCode, appConfig.referralBonus, "whatsapp") },
                            modifier = Modifier.weight(1f).height(50.dp).shadow(15.dp, RoundedCornerShape(16.dp), spotColor = WhatsAppColor.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Button(
                            onClick = { shareIntent(context, refCode, appConfig.referralBonus, "telegram") },
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
                        StepCard("1", "Share Referral Link", "Send your unique code to your friends.")
                        StepCard("2", "Friend Registers", "Your friend applies code and creates an account.")
                        StepCard("3", "Get Instant Reward", "Get bonus coins credited straight to your wallet!")
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
                            Text("12", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Total Invited", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(GlassBorderLight))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("6,000", color = ReferGold, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
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
        putExtra(Intent.EXTRA_SUBJECT, "Join PocketCash Pro!")
        putExtra(
            Intent.EXTRA_TEXT,
            "Hey! I'm earning real money using PocketCash Pro. Join me and use my referral code ${code.ifEmpty { "POCKET500" }} to get a $bonus Coin bonus! Download now."
        )
    }
    try {
        context.startActivity(Intent.createChooser(shareIntent, "Share via..."))
    } catch (e: Exception) {
        Toast.makeText(context, "App not found!", Toast.LENGTH_SHORT).show()
    }
}
