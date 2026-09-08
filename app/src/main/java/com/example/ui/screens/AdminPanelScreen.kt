package com.example.ui.screens

import android.widget.Toast

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.model.AppConfig
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(viewModel: MainViewModel, onBack: () -> Unit, onNavigateToFeedbacks: () -> Unit = {}) {
    val context = LocalContext.current
    val config by viewModel.appConfig.collectAsState()

    var coinValuePer1000 by remember(config) { mutableStateOf(config.coinValuePer1000.toString()) }
    var signupBonus by remember(config) { mutableStateOf(config.signupBonus.toString()) }
    var referralBonus by remember(config) { mutableStateOf(config.referralBonus.toString()) }
    var dailyCheckInReward by remember(config) { mutableStateOf(config.dailyCheckInReward.toString()) }
    var videoReward by remember(config) { mutableStateOf(config.videoReward.toString()) }
    var mathReward by remember(config) { mutableStateOf(config.mathReward.toString()) }
    var captchaReward by remember(config) { mutableStateOf(config.captchaReward.toString()) }
    var minWithdrawCoins by remember(config) { mutableStateOf(config.minWithdrawCoins.toString()) }

    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Global Settings", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = coinValuePer1000,
                onValueChange = { coinValuePer1000 = it },
                label = { Text("Rupees (INR) per 1000 Coins") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = minWithdrawCoins,
                onValueChange = { minWithdrawCoins = it },
                label = { Text("Minimum Coins for Withdrawal") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Rewards & Bonuses", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = signupBonus,
                onValueChange = { signupBonus = it },
                label = { Text("Signup Bonus Coins") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = referralBonus,
                onValueChange = { referralBonus = it },
                label = { Text("Referral Bonus Coins") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dailyCheckInReward,
                onValueChange = { dailyCheckInReward = it },
                label = { Text("Daily Check-in Coins") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Task Rewards", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = videoReward,
                onValueChange = { videoReward = it },
                label = { Text("Video Ad Coins") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = mathReward,
                onValueChange = { mathReward = it },
                label = { Text("Math Quiz Coins") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = captchaReward,
                onValueChange = { captchaReward = it },
                label = { Text("Captcha Coins") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            
                Text("Social Links & Partner App", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                
                var telegramLink by remember { mutableStateOf(config.telegramLink) }
                var whatsappLink by remember { mutableStateOf(config.whatsappLink) }
                var partnerAppName by remember { mutableStateOf(config.partnerAppName) }
                var partnerAppUrl by remember { mutableStateOf(config.partnerAppUrl) }
                
                OutlinedTextField(
                    value = telegramLink,
                    onValueChange = { telegramLink = it },
                    label = { Text("Telegram Link") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = whatsappLink,
                    onValueChange = { whatsappLink = it },
                    label = { Text("WhatsApp Link") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = partnerAppName,
                    onValueChange = { partnerAppName = it },
                    label = { Text("Partner App Name") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = partnerAppUrl,
                    onValueChange = { partnerAppUrl = it },
                    label = { Text("Partner App URL (Play Store / APK link)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

Text("Quiz Game Controls", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                
                var newQuizQuestion by remember { mutableStateOf("") }
                var newQuizOption1 by remember { mutableStateOf("") }
                var newQuizOption2 by remember { mutableStateOf("") }
                var newQuizOption3 by remember { mutableStateOf("") }
                var newQuizOption4 by remember { mutableStateOf("") }
                var newQuizCorrectIndex by remember { mutableStateOf("0") }
                var newQuizReward by remember { mutableStateOf("10") }
                var isAddingQuiz by remember { mutableStateOf(false) }

                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Add New Question", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        OutlinedTextField(value = newQuizQuestion, onValueChange = { newQuizQuestion = it }, label = { Text("Question") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizOption1, onValueChange = { newQuizOption1 = it }, label = { Text("Option 1") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizOption2, onValueChange = { newQuizOption2 = it }, label = { Text("Option 2") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizOption3, onValueChange = { newQuizOption3 = it }, label = { Text("Option 3") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizOption4, onValueChange = { newQuizOption4 = it }, label = { Text("Option 4") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizCorrectIndex, onValueChange = { newQuizCorrectIndex = it }, label = { Text("Correct Index (0-3)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizReward, onValueChange = { newQuizReward = it }, label = { Text("Reward Coins") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                        
                        Button(
                            onClick = {
                                if (newQuizQuestion.isNotEmpty() && newQuizOption1.isNotEmpty()) {
                                    isAddingQuiz = true
                                    val q = com.example.model.QuizQuestion(
                                        question = newQuizQuestion,
                                        options = listOf(newQuizOption1, newQuizOption2, newQuizOption3, newQuizOption4).filter { it.isNotEmpty() },
                                        correctAnswerIndex = newQuizCorrectIndex.toIntOrNull() ?: 0,
                                        reward = newQuizReward.toIntOrNull() ?: 10
                                    )
                                    viewModel.addQuizQuestion(q, onSuccess = {
                                        isAddingQuiz = false
                                        newQuizQuestion = ""
                                        newQuizOption1 = ""
                                        newQuizOption2 = ""
                                        newQuizOption3 = ""
                                        newQuizOption4 = ""
                                        android.widget.Toast.makeText(context, "Added Quiz", android.widget.Toast.LENGTH_SHORT).show()
                                    }, onError = {
                                        isAddingQuiz = false
                                        android.widget.Toast.makeText(context, "Failed to add", android.widget.Toast.LENGTH_SHORT).show()
                                    })
                                }
                            },
                            enabled = !isAddingQuiz,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("Save Question")
                        }
                    }
                }


                Text("Shopping Deals Controls", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                
                var shopCategories by remember { mutableStateOf(config.shopCategories.joinToString(", ")) }
                
                OutlinedTextField(
                    value = shopCategories,
                    onValueChange = { shopCategories = it },
                    label = { Text("Shop Categories (comma separated)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                var newShopTitle by remember { mutableStateOf("") }
                var newShopImageUrl by remember { mutableStateOf("") }
                var newShopAffiliateUrl by remember { mutableStateOf("") }
                var newShopCategory by remember { mutableStateOf("General") }
                var isAddingShopProduct by remember { mutableStateOf(false) }

                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Add New Shop Product", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        OutlinedTextField(value = newShopTitle, onValueChange = { newShopTitle = it }, label = { Text("Product Title") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newShopImageUrl, onValueChange = { newShopImageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newShopAffiliateUrl, onValueChange = { newShopAffiliateUrl = it }, label = { Text("Affiliate URL") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newShopCategory, onValueChange = { newShopCategory = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                        
                        Button(
                            onClick = {
                                if (newShopTitle.isNotEmpty() && newShopAffiliateUrl.isNotEmpty()) {
                                    isAddingShopProduct = true
                                    val p = com.example.model.ShopProduct(
                                        title = newShopTitle,
                                        imageUrl = newShopImageUrl,
                                        affiliateUrl = newShopAffiliateUrl,
                                        category = newShopCategory
                                    )
                                    viewModel.addShopProduct(p, onSuccess = {
                                        isAddingShopProduct = false
                                        newShopTitle = ""
                                        newShopImageUrl = ""
                                        newShopAffiliateUrl = ""
                                        android.widget.Toast.makeText(context, "Added Product", android.widget.Toast.LENGTH_SHORT).show()
                                    }, onError = {
                                        isAddingShopProduct = false
                                        android.widget.Toast.makeText(context, "Failed to add", android.widget.Toast.LENGTH_SHORT).show()
                                    })
                                }
                            },
                            enabled = !isAddingShopProduct,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("Save Product")
                        }
                    }
                }

                            Button(
                onClick = {
                    isSaving = true
                    val newConfig = AppConfig(
                        coinValuePer1000 = coinValuePer1000.toDoubleOrNull() ?: config.coinValuePer1000,
                        signupBonus = signupBonus.toIntOrNull() ?: config.signupBonus,
                        referralBonus = referralBonus.toIntOrNull() ?: config.referralBonus,
                        dailyCheckInReward = dailyCheckInReward.toIntOrNull() ?: config.dailyCheckInReward,
                        videoReward = videoReward.toIntOrNull() ?: config.videoReward,
                        mathReward = mathReward.toIntOrNull() ?: config.mathReward,
                        captchaReward = captchaReward.toIntOrNull() ?: config.captchaReward,
                        minWithdrawCoins = minWithdrawCoins.toIntOrNull() ?: config.minWithdrawCoins,
                        telegramLink = telegramLink,
                        whatsappLink = whatsappLink,
                        partnerAppName = partnerAppName,
                        partnerAppUrl = partnerAppUrl,
                        shopCategories = shopCategories.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    )
                    viewModel.updateAppConfig(
                        newConfig,
                        onSuccess = {
                            isSaving = false
                            Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
                        },
                        onError = { err ->
                            isSaving = false
                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                Text(if (isSaving) "Saving..." else "Save Changes")
            }
        }
    }
}
