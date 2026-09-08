package com.example.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBackground)
    ) {
        // Ambient Background Blurs
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
                .offset(x = 50.dp, y = (-50).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(PremiumSecondary.copy(alpha = 0.2f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x9907090E)) // rgba(7, 9, 14, 0.6) + backdrop filter effect
                    .padding(horizontal = 24.dp, vertical = 22.dp)
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PremiumSurface)
                        .border(1.dp, PremiumOutline, RoundedCornerShape(12.dp))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text("Privacy Policy", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 40.dp)
            ) {
                item {
                    // Header Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(Brush.linearGradient(listOf(PremiumPrimary.copy(alpha = 0.15f), PremiumSecondary.copy(alpha = 0.05f))))
                            .border(1.dp, PremiumOutlineVariant, RoundedCornerShape(22.dp))
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(PremiumPrimary.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = PremiumPrimary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Your Privacy Matters", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Last Updated: October 2026", color = PremiumOnSurfaceVariant, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(22.dp))
                }

                item {
                    PolicySection(
                        icon = Icons.Default.Storage,
                        title = "Information We Collect",
                        description = "To provide earning features and rewards, we collect limited essential details:",
                        list = listOf(
                            "Basic profile info (Name, Phone Number, Email)",
                            "Reward history and task participation logs",
                            "Device ID for fraud prevention and security"
                        )
                    )
                    PolicySection(
                        icon = Icons.Default.Visibility,
                        title = "How We Use Your Data",
                        description = "Your information is solely used to verify task completions, process withdrawal requests, manage leaderboards, and prevent multi-account abuse.",
                        list = emptyList()
                    )
                    PolicySection(
                        icon = Icons.Default.Lock,
                        title = "Data Security & Third Parties",
                        description = "We do not sell your personal data. Limited analytics and ad-reward tracking are securely processed through trusted ad network SDKs (Google AdMob / Unity Ads).",
                        list = emptyList()
                    )
                    PolicySection(
                        icon = Icons.Default.VerifiedUser,
                        title = "User Rights",
                        description = "You have full right to request account deletion or data removal at any time by contacting our support team via email or WhatsApp support.",
                        list = emptyList()
                    )
                }

                item {
                    // Footer Contact
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HorizontalDivider(color = PremiumOutline, modifier = Modifier.padding(bottom = 20.dp))
                        Text("Have any questions regarding privacy?", color = PremiumOnSurfaceVariant, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = PremiumPrimary, modifier = Modifier.size(16.dp))
                            Text("support@pocketcashpro.com", color = PremiumPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PolicySection(icon: ImageVector, title: String, description: String, list: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(PremiumSurface)
            .border(1.dp, PremiumOutline, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = PremiumSecondary, modifier = Modifier.size(16.dp))
            Text(title, color = PremiumSecondary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(description, color = PremiumOnSurfaceVariant, fontSize = 13.sp, lineHeight = 20.sp)
        
        if (list.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(start = 8.dp)) {
                list.forEach { item ->
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("•", color = PremiumOnSurfaceVariant, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(item, color = PremiumOnSurfaceVariant, fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}
