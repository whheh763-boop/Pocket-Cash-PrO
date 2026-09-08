import re

with open('app/src/main/java/com/example/model/Models.kt', 'r') as f:
    content = f.read()

# Add streak properties to User
user_replacement = """    val canCheckIn: Boolean = true,
    val lastResetDate: String = "",
    val lastSpinTime: Long = 0L,
    val currentStreak: Int = 0,
    val totalCheckIns: Int = 0
)"""
content = re.sub(r'    val canCheckIn: Boolean = true,\n    val lastResetDate: String = "",\n    val lastSpinTime: Long = 0L\n\)', user_replacement, content)

# Add social and partner app properties to AppConfig
config_replacement = """    val maintenanceMode: Boolean = false,
    val telegramLink: String = "",
    val whatsappLink: String = "",
    val instagramLink: String = "",
    val youtubeLink: String = "",
    val partnerAppName: String = "SanFlix-Pro",
    val partnerAppUrl: String = "",
    val partnerAppReward: Int = 50,
    val partnerAppIconUrl: String = ""
)"""
content = re.sub(r'    val maintenanceMode: Boolean = false\n\)', config_replacement, content)

# Add QuizQuestion data class
quiz_model = """
data class QuizQuestion(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,
    val reward: Int = 10,
    val isActive: Boolean = true
)
"""
content += quiz_model

with open('app/src/main/java/com/example/model/Models.kt', 'w') as f:
    f.write(content)
