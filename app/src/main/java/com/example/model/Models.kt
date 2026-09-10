package com.example.model

enum class Country(val displayName: String, val currencySymbol: String, val exchangeRatePer1000: Double) {
    INDIA("India", "₹", 10.0),
    NEPAL("Nepal", "NRs", 16.0)
}

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val paymentId: String = "",
    val deviceId: String = "",
    val coinBalance: Int = 0,
    val lifetimeEarnings: Int = 0,
    val country: Country = Country.INDIA,
    val referralCode: String = "",
    val referredBy: String = "",
    val dailyMathLimit: Int = 15,
    val dailyCaptchaLimit: Int = 20,
    val dailyFreeSpinsLeft: Int = 10,
    val dailyAdSpinsLeft: Int = 20,
    val dailyFreeScratchLeft: Int = 10,
    val dailyAdScratchLeft: Int = 20,
    val dailyFreeMathQuizLeft: Int = 10,
    val dailyAdMathQuizLeft: Int = 15,
    val dailyFreeGkQuizLeft: Int = 10,
    val dailyAdGkQuizLeft: Int = 15,
    val canCheckIn: Boolean = true,
    val lastResetDate: String = "",
    val lastSpinTime: Long = 0L,
    val currentStreak: Int = 0,
    val totalCheckIns: Int = 0,
    val completedQuizzes: List<String> = emptyList(),
    val totalSpins: Int = 0,
    val referralMilestoneCompleted: Boolean = false,
    val ipAddress: String = ""

)

data class Transaction(
    val id: String = "",
    val title: String = "",
    val amount: Int = 0,
    val isCredit: Boolean = true, // true for earning, false for withdrawal
    val status: String = "", // "Completed", "Pending", "Failed"
    val timestamp: Long = 0L
)

data class AppConfig(
    val videoReward: Int = 15,
    val mathReward: Int = 10,
    val captchaReward: Int = 5,
    val dailyCheckInReward: Int = 20,
    val spinCost: Int = 0,
    val signupBonus: Int = 50,
    val referralBonus: Int = 100,
    val minWithdrawCoins: Int = 1000,
    val coinValuePer1000: Double = 10.0,
    val maintenanceMode: Boolean = false,
    val useRealAds: Boolean = false,
    val telegramLink: String = "",
    val whatsappLink: String = "",
    val instagramLink: String = "",
    val youtubeLink: String = "",
    val partnerAppName: String = "SanFlix-Pro",
    val partnerAppUrl: String = "",
    val partnerAppReward: Int = 50,
    val partnerAppIconUrl: String = "",
    val shopCategories: List<String> = listOf("General", "Gaming", "Electronics", "Fashion")
)

data class QuizQuestion(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,
    val reward: Int = 10,
    val isActive: Boolean = true
)

data class ShopProduct(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val affiliateUrl: String = "",
    val category: String = "General"
)

data class Feedback(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val reason: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)

data class SecureQuizData(
    val id: String = "",
    val type: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val rewardCoins: Int = 2
)
