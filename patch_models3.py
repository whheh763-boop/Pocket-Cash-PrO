import re

with open('app/src/main/java/com/example/model/Models.kt', 'r') as f:
    content = f.read()

user_replacement = """    val lastSpinTime: Long = 0L,
    val currentStreak: Int = 0,
    val totalCheckIns: Int = 0,
    val completedQuizzes: List<String> = emptyList()
)"""
content = content.replace("val totalCheckIns: Int = 0\n)", user_replacement)

with open('app/src/main/java/com/example/model/Models.kt', 'w') as f:
    f.write(content)
