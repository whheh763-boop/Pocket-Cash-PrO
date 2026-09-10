import re

with open('app/src/main/java/com/example/model/Models.kt', 'r') as f:
    content = f.read()

old_user = """    val dailyMathLimit: Int = 15,
    val dailyCaptchaLimit: Int = 20,
    val dailyFreeSpinsLeft: Int = 10,
    val dailyAdSpinsLeft: Int = 20,
    val dailyFreeScratchLeft: Int = 10,
    val dailyAdScratchLeft: Int = 20,"""

new_user = """    val dailyMathLimit: Int = 15,
    val dailyCaptchaLimit: Int = 20,
    val dailyFreeSpinsLeft: Int = 10,
    val dailyAdSpinsLeft: Int = 20,
    val dailyFreeScratchLeft: Int = 10,
    val dailyAdScratchLeft: Int = 20,
    val dailyFreeMathQuizLeft: Int = 10,
    val dailyAdMathQuizLeft: Int = 15,
    val dailyFreeGkQuizLeft: Int = 10,
    val dailyAdGkQuizLeft: Int = 15,"""

if "dailyFreeMathQuizLeft" not in content:
    content = content.replace(old_user, new_user)

secure_quiz_model = """
data class SecureQuizData(
    val id: String = "",
    val type: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val rewardCoins: Int = 2
)
"""

if "SecureQuizData" not in content:
    content += secure_quiz_model

with open('app/src/main/java/com/example/model/Models.kt', 'w') as f:
    f.write(content)
