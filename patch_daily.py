import re

with open('app/src/main/java/com/example/ui/screens/DailyRewardsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("BackgroundDark", "com.example.ui.theme.PremiumBackground")

with open('app/src/main/java/com/example/ui/screens/DailyRewardsScreen.kt', 'w') as f:
    f.write(content)
