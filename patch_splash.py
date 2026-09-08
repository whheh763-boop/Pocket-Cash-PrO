import re

with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'r') as f:
    content = f.read()

if "import androidx.compose.ui.draw.clip" not in content:
    content = content.replace("import androidx.compose.ui.draw.alpha", "import androidx.compose.ui.draw.alpha\nimport androidx.compose.ui.draw.clip\nimport androidx.compose.foundation.shape.RoundedCornerShape")

old_modifier = """                modifier = Modifier
                    .size(150.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)"""

new_modifier = """                modifier = Modifier
                    .size(150.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
                    .clip(RoundedCornerShape(32.dp))"""

content = content.replace(old_modifier, new_modifier)

with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'w') as f:
    f.write(content)
