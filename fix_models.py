import re

with open('app/src/main/java/com/example/model/Models.kt', 'r') as f:
    content = f.read()

user_class = """data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "Guest User",
    val paymentId: String = "",
    val deviceId: String = "",
    val coinBalance: Int = 0,
    val lifetimeEarnings: Int = 0,
    val country: Country = Country.INDIA,
    val referralCode: String = "",
    val referredBy: String = "",
    val dailyMathLimit: Int = 15,
    val dailyCaptchaLimit: Int = 20,
    val canCheckIn: Boolean = true,
    val lastResetDate: String = "",
    val lastSpinTime: Long = 0L,
    val currentStreak: Int = 0,
    val totalCheckIns: Int = 0,
    val completedQuizzes: List<String> = emptyList()
)"""

content = re.sub(r'data class User\([\s\S]*?\)\n', user_class + '\n', content)

with open('app/src/main/java/com/example/model/Models.kt', 'w') as f:
    f.write(content)
