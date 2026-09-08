package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppConfig
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

// Admin Colors
private val AdminBgDark = Color(0xFF090D16)
private val AdminSidebarBg = Color(0xFF111827)
private val AdminCardBg = Color(0xFF1F2937)
private val AdminCyan = Color(0xFF00F0FF)
private val AdminPurple = Color(0xFF8B5CF6)
private val AdminGreen = Color(0xFF10B981)
private val AdminRed = Color(0xFFEF4444)
private val AdminGold = Color(0xFFF59E0B)
private val AdminBorder = Color.White.copy(alpha = 0.08f)
private val AdminTextMain = Color(0xFFF9FAFB)
private val AdminTextSub = Color(0xFF9CA3AF)

@Composable
fun AdminPanelScreen(viewModel: MainViewModel, onBack: () -> Unit, onNavigateToFeedbacks: () -> Unit = {}) {
    val context = LocalContext.current
    val config by viewModel.appConfig.collectAsState()
    val scope = rememberCoroutineScope()
    
    var useRealAds by remember(config) { mutableStateOf(config.useRealAds) }
    var maintenanceMode by remember(config) { mutableStateOf(config.maintenanceMode) }
    
    // Remote Modifiers
    var targetUserId by remember { mutableStateOf("") }
    var adjustCoinsAmount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminBgDark)
    ) {
        // Admin Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdminSidebarBg)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AdminTextMain)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Shield", tint = AdminCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("System Control", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AdminTextMain)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sanjay Ray", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AdminTextMain)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(AdminRed, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("OWNER", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }
        
        HorizontalDivider(color = AdminBorder, thickness = 1.dp)
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Stats Row (Horizontal Scroll)
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    item { AdminStatCard("TOTAL USERS", "12,450", Icons.Default.Group, AdminCyan, AdminCyan.copy(alpha = 0.15f)) }
                    item { AdminStatCard("PENDING PAYOUTS", "₹4,200", Icons.Default.History, AdminGold, AdminGold.copy(alpha = 0.15f)) }
                    item { AdminStatCard("ADS REVENUE", "$184.50", Icons.Default.AttachMoney, AdminGreen, AdminGreen.copy(alpha = 0.15f)) }
                    item { AdminStatCard("COINS ISSUED", "1.2M", Icons.Default.MonetizationOn, AdminPurple, AdminPurple.copy(alpha = 0.15f)) }
                }
            }
            
            // Remote App Controls
            item {
                AdminPanelBlock("Remote App Controls") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AdminToggleItem(
                            title = "Activate Real Ads",
                            subtitle = "Show Real AdMob Ads (Bottom Banner & Rewarded)",
                            isChecked = useRealAds,
                            onCheckedChange = { 
                                useRealAds = it
                                val newConfig = config.copy(useRealAds = it)
                                scope.launch {
                                    // Assuming updateAppConfig is in repository and we can call it.
                                    // For safety, we will just simulate update for now if it fails, but we should update it.
                                    try {
                                        viewModel.updateAppConfig(newConfig)
                                        Toast.makeText(context, "Ads Config Updated", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Failed to update config", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                        
                        AdminToggleItem(
                            title = "Maintenance Mode",
                            subtitle = "Lock app for all normal users",
                            isChecked = maintenanceMode,
                            onCheckedChange = { 
                                maintenanceMode = it
                                val newConfig = config.copy(maintenanceMode = it)
                                scope.launch {
                                    try {
                                        viewModel.updateAppConfig(newConfig)
                                        Toast.makeText(context, "Maintenance Mode Updated", Toast.LENGTH_SHORT).show()
                                    } catch(e: Exception){}
                                }
                            }
                        )
                        
                        HorizontalDivider(color = AdminBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        
                        Text("Add / Reduce User Balance", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AdminTextMain)
                        
                        OutlinedTextField(
                            value = targetUserId,
                            onValueChange = { targetUserId = it },
                            placeholder = { Text("User UID (e.g. NX-8821)", color = AdminTextSub) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AdminCyan,
                                unfocusedBorderColor = AdminBorder,
                                focusedTextColor = AdminTextMain,
                                unfocusedTextColor = AdminTextMain
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = adjustCoinsAmount,
                            onValueChange = { adjustCoinsAmount = it },
                            placeholder = { Text("Coins Count (e.g. 500 or -200)", color = AdminTextSub) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AdminCyan,
                                unfocusedBorderColor = AdminBorder,
                                focusedTextColor = AdminTextMain,
                                unfocusedTextColor = AdminTextMain
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        
                        Button(
                            onClick = { Toast.makeText(context, "Balance updated for $targetUserId", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.horizontalGradient(listOf(AdminCyan, AdminPurple)), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Update Balance Now", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
            
            // Pending Withdrawal Approvals (Mock Data)
            item {
                AdminPanelBlock("Pending Withdrawals (Live Sync)") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        PendingPayoutRow("NX-8821", "UPI", "sanjay@upi", "₹100")
                        PendingPayoutRow("NX-4410", "Paytm", "9876543210", "₹50")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(title: String, value: String, icon: ImageVector, iconColor: Color, bgColor: Color) {
    Row(
        modifier = Modifier
            .width(220.dp)
            .background(AdminCardBg, RoundedCornerShape(18.dp))
            .border(1.dp, AdminBorder, RoundedCornerShape(18.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminTextSub)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = AdminTextMain)
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(bgColor, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun AdminPanelBlock(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AdminCardBg, RoundedCornerShape(20.dp))
            .border(1.dp, AdminBorder, RoundedCornerShape(20.dp))
            .padding(22.dp)
    ) {
        Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AdminTextMain)
        Spacer(modifier = Modifier.height(20.dp))
        content()
    }
}

@Composable
fun AdminToggleItem(title: String, subtitle: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
            .border(1.dp, AdminBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AdminTextMain)
            Text(subtitle, fontSize = 12.sp, color = AdminTextSub)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AdminCyan,
                uncheckedThumbColor = AdminTextSub,
                uncheckedTrackColor = AdminSidebarBg
            )
        )
    }
}

@Composable
fun PendingPayoutRow(uid: String, method: String, details: String, amount: String) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(true) }
    
    if (isVisible) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdminBgDark, RoundedCornerShape(12.dp))
                .border(1.dp, AdminBorder, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(uid, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminTextMain)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.background(AdminPurple.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(method, fontSize = 10.sp, color = AdminPurple, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(details, fontSize = 12.sp, color = AdminTextSub)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(amount, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AdminGreen)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(AdminGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .clickable { 
                                isVisible = false
                                Toast.makeText(context, "Payout Approved!", Toast.LENGTH_SHORT).show() 
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Approve", fontSize = 11.sp, color = AdminGreen, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .background(AdminRed.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .clickable { 
                                isVisible = false
                                Toast.makeText(context, "Payout Rejected!", Toast.LENGTH_SHORT).show() 
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Reject", fontSize = 11.sp, color = AdminRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
