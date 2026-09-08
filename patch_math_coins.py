import re

with open('app/src/main/java/com/example/ui/screens/MathEarnScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("uiState.coins", "uiState.coinBalance")

with open('app/src/main/java/com/example/ui/screens/MathEarnScreen.kt', 'w') as f:
    f.write(content)

