import re

content = """package com.example.ui.screens

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Country
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Custom Brand Colors
val UpiColor = Color(0xFF5F259F)
val PaytmColor = Color(0xFF00BAF2)
val AmazonColor = Color(0xFFFF9900)
val RechargeColor = Color(0xFF10B981)

val EsewaColor = Color(0xFF60BB46)
val KhaltiColor = Color(0xFF5C2D91)
val ImeColor = Color(0xFFE21B23)
val NtcColor = Color(0xFF005BB7)

data class PayoutMethod(
    val id: String,
    val title: String,
    val description: String,
    val brandColor: Color,
    val iconLetter: String? = null,
    val iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null
)

@Composable
fun WalletScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val userState by viewModel.userState.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val minWithdrawalCoins = appConfig.minWithdrawCoins
    val scope = rememberCoroutineScope()
    var withdrawalStatus by remember { mutableStateOf("") }
    
    var manualCountryTab by remember { mutableStateOf<Country?>(null) }
    val currentCountry = manualCountryTab ?: userState.country
    
    val methods = if (currentCountry == Country.INDIA) {
        listOf(
            PayoutMethod("upi", "UPI (PhonePe, GPay, BHIM)", "Instant Direct Bank • Min $minWithdrawalCoins Coins", UpiColor, iconVector = Icons.Default.QrCode),
            PayoutMethod("paytm", "Paytm Wallet", "Fast Wallet Credit • Min 500 Coins", PaytmColor, "Paytm"),
            PayoutMethod("amazon", "Amazon Pay Gift Card", "Instant Voucher Code • Min $minWithdrawalCoins Coins", AmazonColor, iconVector = Icons.Default.CardGiftcard),
            PayoutMethod("recharge_in", "Mobile Topup (Jio / Airtel / Vi)", "Direct Prepaid Topup • Min 200 Coins", RechargeColor, iconVector = Icons.Default.PhoneAndroid)
        )
    } else {
        listOf(
            PayoutMethod("esewa", "eSewa Wallet", "Instant Transfer • Min $minWithdrawalCoins Coins", EsewaColor, "e"),
            PayoutMethod("khalti", "Khalti Wallet", "Fast Payout • Min $minWithdrawalCoins Coins", KhaltiColor, "K"),
            PayoutMethod("ime", "IME Pay", "Direct Wallet Payout", ImeColor, "IME"),
            PayoutMethod("recharge_np", "Mobile Topup (Ncell / NTC)", "Min 500 Coins = NPR 50 Recharge", NtcColor, iconVector = Icons.Default.PhoneAndroid)
        )
    }
    
    var selectedMethod by remember { mutableStateOf("") }
    
    LaunchedEffect(currentCountry) {
        selectedMethod = "" // Reset method when country changes
    }
    
    if (selectedMethod.isEmpty() && methods.isNotEmpty()) {
        selectedMethod = methods.first().id
    }
    var accountId by remember { mutableStateOf("") }

    val rate = if (currentCountry == Country.NEPAL) appConfig.coinValuePer1000 * 1.6 else appConfig.coinValuePer1000
    val convertedValue = (userState.coinBalance / 1000.0) * rate

    val animatedCoins by animateIntAsState(
        targetValue = userState.coinBalance,
        animationSpec = tween(1000),
        label = "walletCoinAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = (-50).dp)
                .size(250.dp)
                .blur(100.dp)
                .background(PremiumPrimary.copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = (-150).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(PremiumSecondary.copy(alpha = 0.15f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 22.dp)
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Wallet & Withdraw", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 40.dp)
            ) {
                item {
                    // Country Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 22.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                            .padding(4.dp)
                    ) {
                        CountryTabButton(
                            text = "🇮🇳 India",
                            isActive = currentCountry == Country.INDIA,
                            onClick = { manualCountryTab = Country.INDIA },
                            modifier = Modifier.weight(1f)
                        )
                        CountryTabButton(
                            text = "🇳🇵 Nepal",
                            isActive = currentCountry == Country.NEPAL,
                            onClick = { manualCountryTab = Country.NEPAL },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    // Balance Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 22.dp)
                            .shadow(25.dp, RoundedCornerShape(24.dp), spotColor = PremiumPrimary.copy(alpha = 0.3f))
                            .clip(RoundedCornerShape(24.dp))
                            .background(Brush.linearGradient(listOf(PremiumPrimary.copy(alpha = 0.15f), PremiumSecondary.copy(alpha = 0.05f))))
                            .border(1.dp, PremiumOutlineVariant, RoundedCornerShape(24.dp))
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("AVAILABLE BALANCE", color = PremiumOnSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                Icon(Icons.Default.Toll, contentDescription = null, tint = IconTintYellow, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("$animatedCoins", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(PremiumSecondary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .border(1.dp, PremiumSecondary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            val symbol = if (currentCountry == Country.INDIA) "₹" else "NPR"
                            Text("= $symbol${String.format("%.2f", convertedValue)}", color = PremiumSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Text("SELECT PAYOUT METHOD", color = PremiumOnSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                }

                items(methods.size) { index ->
                    val method = methods[index]
                    val isSelected = selectedMethod == method.id
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) PremiumSecondary.copy(alpha = 0.08f) else PremiumSurface)
                            .border(1.dp, if (isSelected) PremiumSecondary else PremiumOutline, RoundedCornerShape(18.dp))
                            .clickable { selectedMethod = method.id }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(method.brandColor, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (method.iconVector != null) {
                                    Icon(method.iconVector, contentDescription = null, tint = if(method.brandColor == AmazonColor) Color.Black else Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text(method.iconLetter ?: "", color = Color.White, fontSize = if((method.iconLetter?.length ?: 0) > 3) 12.sp else 16.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                            Column {
                                Text(method.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(method.description, color = PremiumOnSurfaceVariant, fontSize = 11.sp)
                            }
                        }
                        // Radio Dot
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(2.dp, if (isSelected) PremiumSecondary else PremiumOutlineVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(modifier = Modifier.size(10.dp).background(PremiumSecondary, CircleShape))
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        if (currentCountry == Country.INDIA) "Enter UPI ID / Mobile Number" else "Enter eSewa / Khalti Mobile Number", 
                        color = PremiumOnSurfaceVariant, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = accountId,
                        onValueChange = { accountId = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 22.dp),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text(if (currentCountry == Country.INDIA) "e.g. 9876543210@ybl" else "e.g. 98XXXXXXXX", color = PremiumOnSurfaceVariant) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PremiumPrimary,
                            unfocusedBorderColor = PremiumOutlineVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = PremiumSurface,
                            unfocusedContainerColor = PremiumSurface
                        ),
                        singleLine = true
                    )
                }

                item {
                    val isEnabled = userState.coinBalance >= minWithdrawalCoins && accountId.isNotEmpty()
                    val symbol = if (currentCountry == Country.INDIA) "₹" else "NPR"
                    
                    Button(
                        onClick = {
                            scope.launch {
                                withdrawalStatus = "Processing request..."
                                delay(1500)
                                withdrawalStatus = "Withdrawal request submitted for Admin approval."
                            }
                        },
                        enabled = isEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .height(56.dp)
                            .shadow(15.dp, RoundedCornerShape(18.dp), spotColor = PremiumPrimary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent, // Managed by Box gradient
                            disabledContainerColor = PremiumSurfaceVariant
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (isEnabled) Brush.linearGradient(listOf(PremiumPrimary, PremiumPrimaryDark))
                                    else Brush.linearGradient(listOf(PremiumSurfaceVariant, PremiumSurfaceVariant)),
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (isEnabled) "Withdraw Money ($symbol${String.format("%.2f", convertedValue)})" else "Min $minWithdrawalCoins Coins Required",
                                color = if (isEnabled) Color.White else PremiumOnSurfaceVariant,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                    
                    if (withdrawalStatus.isNotEmpty()) {
                        Text(
                            withdrawalStatus, 
                            color = PremiumPrimary, 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNavigateToHistory)
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = PremiumOnSurfaceVariant, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Transaction History", color = PremiumOnSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CountryTabButton(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7))) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isActive) Color.White else Color(0xFF94A3B8),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
"""

with open('app/src/main/java/com/example/ui/screens/WalletScreen.kt', 'w') as f:
    f.write(content)
