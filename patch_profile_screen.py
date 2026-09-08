import re

content = """package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val userState by viewModel.userState.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
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
                Text("My Account", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                IconButtonGlass(icon = Icons.Default.Settings, onClick = {
                    if (userState.isAdmin) onNavigateToAdmin() else Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
                })
            }
        }

        item {
            // Profile Overview Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .shadow(30.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.3f))
                    .clip(RoundedCornerShape(28.dp))
                    .background(PremiumSurface)
                    .border(1.dp, PremiumOutlineVariant, RoundedCornerShape(28.dp))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(2.dp, PremiumPrimary, RoundedCornerShape(20.dp))
                    )
                    // Online Badge
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = 2.dp, y = 2.dp)
                            .background(IconTintGreen, CircleShape)
                            .border(2.dp, PremiumBackground, CircleShape)
                    )
                }
                
                Column {
                    Text(
                        userState.displayName.ifEmpty { "Guest User" },
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = PremiumSecondary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(userState.uid.take(10), color = PremiumOnSurfaceVariant, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = PremiumPrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(userState.email.ifEmpty { "admin@gmail.com" }, color = PremiumOnSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            // Partner App (Movie Card)
            Text(
                "EXCLUSIVE PARTNER APP",
                color = PremiumOnSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .shadow(25.dp, RoundedCornerShape(22.dp), spotColor = MovieAccent.copy(alpha = 0.15f))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(listOf(MovieAccent.copy(alpha = 0.2f), Color(0xFF0F172A).copy(alpha = 0.6f))))
                    .border(1.dp, MovieAccent.copy(alpha = 0.3f), RoundedCornerShape(22.dp))
                    .clickable {
                        if (appConfig.partnerAppUrl.isNotEmpty()) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appConfig.partnerAppUrl)))
                        }
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(MovieAccent.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = MovieAccent, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(appConfig.partnerAppName.ifEmpty { "CineMax Movies" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.background(MovieAccent, RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("FREE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Watch latest movies & earn ${appConfig.partnerAppReward} coins", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    }
                }
                Icon(Icons.Default.PlayCircle, contentDescription = null, tint = MovieAccent, modifier = Modifier.size(26.dp))
            }
        }

        item {
            // Community & Support
            Text(
                "COMMUNITY & SUPPORT",
                color = PremiumOnSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "WhatsApp", color = IconTintWhatsapp, icon = Icons.Default.Chat) {
                    if (appConfig.whatsappLink.isNotEmpty()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appConfig.whatsappLink)))
                }
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "Telegram", color = IconTintTelegram, icon = Icons.Default.Send) {
                    if (appConfig.telegramLink.isNotEmpty()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appConfig.telegramLink)))
                }
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "Instagram", color = IconTintInstagram, icon = Icons.Default.CameraAlt) {
                    if (appConfig.instagramLink.isNotEmpty()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appConfig.instagramLink)))
                }
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "YouTube", color = IconTintYoutube, icon = Icons.Default.PlayArrow) {
                    if (appConfig.youtubeLink.isNotEmpty()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appConfig.youtubeLink)))
                }
            }
        }

        item {
            // Help & Legal
            Text(
                "HELP & LEGAL",
                color = PremiumOnSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(PremiumSurface)
                    .border(1.dp, PremiumOutline, RoundedCornerShape(22.dp))
            ) {
                SettingsRowPremium(
                    icon = Icons.Default.Email,
                    title = "Send Feedback",
                    subtitle = "Tell us how we can improve",
                    iconTint = IconTintBlue,
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply { data = Uri.parse("mailto:support@pocketcash.com") }
                        context.startActivity(intent)
                    }
                )
                HorizontalDivider(color = PremiumOutline, modifier = Modifier.padding(horizontal = 20.dp))
                SettingsRowPremium(
                    icon = Icons.Default.Security,
                    title = "Privacy Policy",
                    subtitle = "Your data protection & terms",
                    iconTint = IconTintYellow,
                    onClick = { Toast.makeText(context, "Privacy Policy", Toast.LENGTH_SHORT).show() }
                )
                HorizontalDivider(color = PremiumOutline, modifier = Modifier.padding(horizontal = 20.dp))
                SettingsRowPremium(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Log Out",
                    subtitle = "Account security",
                    iconTint = IconTintRed,
                    titleColor = IconTintRed,
                    onClick = {
                        viewModel.logout()
                        onLogout()
                    }
                )
            }
        }
    }
}

@Composable
fun SocialBtnPremium(modifier: Modifier = Modifier, name: String, color: Color, icon: ImageVector, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PremiumSurface)
            .border(1.dp, PremiumOutline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
        Text(name, color = Color.White, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
fun SettingsRowPremium(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    titleColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(title, color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = PremiumOnSurfaceVariant, fontSize = 11.sp)
            }
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = PremiumOnSurfaceVariant, modifier = Modifier.size(18.dp))
    }
}
"""

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)
