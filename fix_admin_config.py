import re

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'r') as f:
    content = f.read()

bad_config = """                    val newConfig = AppConfig(
                        coinValuePer1000 = coinValuePer1000.toDoubleOrNull() ?: config.coinValuePer1000,
                        signupBonus = signupBonus.toIntOrNull() ?: config.signupBonus,
                        referralBonus = referralBonus.toIntOrNull() ?: config.referralBonus,
                        dailyCheckInReward = dailyCheckInReward.toIntOrNull() ?: config.dailyCheckInReward,
                        videoReward = videoReward.toIntOrNull() ?: config.videoReward,
                        mathReward = mathReward.toIntOrNull() ?: config.mathReward,
                        captchaReward = captchaReward.toIntOrNull() ?: config.captchaReward,
                        minWithdrawCoins = minWithdrawCoins.toIntOrNull() ?: config.minWithdrawCoins
                    )"""

good_config = """                    val newConfig = AppConfig(
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
                    )"""

content = content.replace(bad_config, good_config)

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'w') as f:
    f.write(content)
