package com.example.ui.screens

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
import androidx.compose.ui.window.Dialog
import com.example.model.Country
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val PaytmColor = Color(0xFF00BAF2)
val UpiColor = Color(0xFF6366F1)
val EsewaColor = Color(0xFF60BB46)
val KhaltiColor = Color(0xFF5C2D91)

data class PayoutMethod(
    val id: String,
    val name: String,
    val sub: String,
    val brandColor: Color,
    val iconVector: androidx.compose.ui.graphics.vector.ImageVector
)

data class AmountChipData(val value: Int, val coins: Int)

@Composable
fun WalletScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val userState by viewModel.userState.collectAsState()
    val scope = rememberCoroutineScope()
    
    var selectedCountry by remember { mutableStateOf(Country.INDIA) }
    var selectedMethod by remember { mutableStateOf("paytm") }
    var selectedRs by remember { mutableStateOf(10) }
    var selectedCoinsNeeded by remember { mutableStateOf(1000) }
    var accountId by remember { mutableStateOf("") }
    
    var showResultModal by remember { mutableStateOf(false) }

    val methodsIN = listOf(
        PayoutMethod("paytm", "Paytm", "Wallet Transfer", PaytmColor, Icons.Default.PhoneAndroid),
        PayoutMethod("upi", "UPI Direct", "GPay, PhonePe", UpiColor, Icons.Default.QrCode)
    )
    val methodsNP = listOf(
        PayoutMethod("esewa", "eSewa", "eSewa Wallet", EsewaColor, Icons.Default.AccountBalanceWallet),
        PayoutMethod("khalti", "Khalti", "Digital Wallet", KhaltiColor, Icons.Default.AccountBalance)
    )

    val amountsIN = listOf(
        AmountChipData(10, 1000), AmountChipData(50, 5000), AmountChipData(100, 10000)
    )
    val amountsNP = listOf(
        AmountChipData(20, 1250), AmountChipData(100, 6250), AmountChipData(200, 12500)
    )

    val activeMethods = if (selectedCountry == Country.INDIA) methodsIN else methodsNP
    val activeAmounts = if (selectedCountry == Country.INDIA) amountsIN else amountsNP
    val symbol = if (selectedCountry == Country.INDIA) "₹" else "रू "
    val rateText = if (selectedCountry == Country.INDIA) "100 Coins = ₹1 INR" else "100 Coins = रू 1.6 NPR"
    val coinsPerUnit = if (selectedCountry == Country.INDIA) 100.0 else 62.5
    
    val convertedVal = ((userState?.coinBalance ?: 0) / coinsPerUnit)

    LaunchedEffect(selectedCountry) {
        selectedMethod = activeMethods.first().id
        selectedRs = activeAmounts.first().value
        selectedCoinsNeeded = activeAmounts.first().coins
        accountId = ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07090E))
    ) {
        // Ambient Glows
        Box(modifier = Modifier.offset(x = (-40).dp, y = (-40).dp).size(260.dp).blur(100.dp).background(PaytmColor.copy(alpha = 0.25f), CircleShape))
        Box(modifier = Modifier.align(Alignment.BottomEnd).offset(x = 50.dp, y = (-100).dp).size(280.dp).blur(100.dp).background(Color(0xFF6366F1).copy(alpha = 0.25f), CircleShape))

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Wallet, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Redeem Cash", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }

            // Country Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 5.dp)
                    .background(Color(0xFF0F172A).copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).clickable { selectedCountry = Country.INDIA }.background(if (selectedCountry == Country.INDIA) Color.White.copy(alpha = 0.1f) else Color.Transparent).border(if (selectedCountry == Country.INDIA) 1.dp else 0.dp, if (selectedCountry == Country.INDIA) Color.White.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(12.dp)).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Text("🇮🇳 India", color = if (selectedCountry == Country.INDIA) Color.White else Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).clickable { selectedCountry = Country.NEPAL }.background(if (selectedCountry == Country.NEPAL) Color.White.copy(alpha = 0.1f) else Color.Transparent).border(if (selectedCountry == Country.NEPAL) 1.dp else 0.dp, if (selectedCountry == Country.NEPAL) Color.White.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(12.dp)).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Text("🇳🇵 Nepal", color = if (selectedCountry == Country.NEPAL) Color.White else Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
            ) {
                item {
                    // Balance Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF1E293B).copy(alpha = 0.7f), Color(0xFF0F172A).copy(alpha = 0.8f))))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text("AVAILABLE BALANCE", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("${userState?.coinBalance ?: 0}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFACC15))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Coins", fontSize = 16.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(bottom = 6.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("(≈ $symbol${String.format("%.2f", convertedVal)})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E), modifier = Modifier.padding(bottom = 6.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(modifier = Modifier.background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                                Text("⚡ Conversion: $rateText", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    Text("Select Method", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        activeMethods.forEach { method ->
                            val isActive = selectedMethod == method.id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isActive) method.brandColor.copy(alpha = 0.15f) else Color(0xFF161B26).copy(alpha = 0.6f))
                                    .border(1.dp, if (isActive) method.brandColor else Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                                    .clickable { selectedMethod = method.id }
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(38.dp).background(method.brandColor, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                        Icon(method.iconVector, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(method.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                        Text(method.sub, color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    Text("Account Details", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = accountId,
                        onValueChange = { accountId = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(if (selectedMethod == "upi") "e.g. mobile@upi" else "Enter 10 digit number", color = Color(0xFF94A3B8)) },
                        leadingIcon = {
                            Icon(if (selectedMethod == "upi") Icons.Default.AlternateEmail else Icons.Default.Phone, contentDescription = null, tint = Color(0xFF22D3EE))
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White.copy(alpha = 0.15f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.6f),
                            unfocusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.6f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    Text("Select Amount", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        activeAmounts.forEach { amt ->
                            val isActive = selectedRs == amt.value
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isActive) Color(0xFFFACC15).copy(alpha = 0.15f) else Color(0xFF161B26).copy(alpha = 0.6f))
                                    .border(1.dp, if (isActive) Color(0xFFFACC15) else Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                    .clickable { 
                                        selectedRs = amt.value
                                        selectedCoinsNeeded = amt.coins
                                    }
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("$symbol${amt.value}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                    Text("%,d Coins".format(amt.coins), fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                }

                item {
                    val isEnabled = (userState?.coinBalance ?: 0) >= selectedCoinsNeeded && accountId.isNotEmpty()
                    
                    Button(
                        onClick = {
                            if (isEnabled) {
                                viewModel.addCoins(-selectedCoinsNeeded, "Withdrawal: $selectedMethod")
                                scope.launch { showResultModal = true }
                            }
                        },
                        enabled = isEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Gray.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(Color(0xFF22D3EE), Color(0xFF6366F1))), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("WITHDRAW CASH NOW", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onNavigateToHistory() }.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Recent Withdrawals", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showResultModal) {
        Dialog(onDismissRequest = { showResultModal = false; accountId = "" }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF07090E).copy(alpha = 0.96f), RoundedCornerShape(36.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(36.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(75.dp).background(Color(0xFF22C55E).copy(alpha = 0.15f), CircleShape).border(1.dp, Color(0xFF22C55E), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("Request Submitted!", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Aapka payment 2-24 hours ke andar $selectedMethod me transfer ho jayega.", fontSize = 14.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(top = 5.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(25.dp))
                    Button(
                        onClick = { showResultModal = false; accountId = "" },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF22D3EE), Color(0xFF6366F1))), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                            Text("DONE", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
