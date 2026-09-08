import re

with open('app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('onNavigateToMathEarn = { rootNavController.navigate("math_earn") }', 'onNavigateToMathEarn = { rootNavController.navigate("math_blitz") }')

with open('app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)
