import re

with open('app/src/main/java/com/example/model/Models.kt', 'r') as f:
    content = f.read()

old_user = """    val dailyFreeSpinsLeft: Int = 10,
    val dailyAdSpinsLeft: Int = 20,"""

new_user = """    val dailyFreeSpinsLeft: Int = 10,
    val dailyAdSpinsLeft: Int = 20,
    val dailyFreeScratchLeft: Int = 10,
    val dailyAdScratchLeft: Int = 20,"""

if "dailyFreeScratchLeft" not in content:
    content = content.replace(old_user, new_user)

with open('app/src/main/java/com/example/model/Models.kt', 'w') as f:
    f.write(content)
