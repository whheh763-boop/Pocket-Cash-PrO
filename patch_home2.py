import re

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("onNavigateToQuiz: () -> Unit,", "onNavigateToQuiz: () -> Unit,\n    onNavigateToDailyRewards: () -> Unit,")
content = content.replace("onClick = { onNavigateToQuiz() }, // Will change this to DailyRewards route later", "onClick = { onNavigateToDailyRewards() },")

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'w') as f:
    f.write(content)
