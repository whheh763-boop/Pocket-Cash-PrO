import re

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('title = "Math Earn"', 'title = "Math Blitz"')
content = content.replace('icon = Icons.Default.Security,\n                    iconTint = Color(0xFF22D3EE),\n                    bgColor = Color(0xFF22D3EE).copy(alpha = 0.1f),\n                    onClick = onNavigateToMathEarn', 'icon = Icons.Default.Bolt,\n                    iconTint = Color(0xFFFACC15),\n                    bgColor = Color(0xFFFACC15).copy(alpha = 0.1f),\n                    onClick = onNavigateToMathEarn')

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)
