import re

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'r') as f:
    content = f.read()

# I will append new text fields below "Coin value per 1000"
new_fields = """
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
"""

content = content.replace("Spacer(modifier = Modifier.height(24.dp))", new_fields + "\n                Spacer(modifier = Modifier.height(24.dp))")

# Update the save button logic to include new fields
save_old = """                                referralBonus = refBonus,
                                minWithdrawCoins = minWithdraw,
                                coinValuePer1000 = coinValue
                            )"""

save_new = """                                referralBonus = refBonus,
                                minWithdrawCoins = minWithdraw,
                                coinValuePer1000 = coinValue,
                                telegramLink = telegramLink,
                                whatsappLink = whatsappLink,
                                partnerAppName = partnerAppName,
                                partnerAppUrl = partnerAppUrl
                            )"""

content = content.replace(save_old, save_new)

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'w') as f:
    f.write(content)
