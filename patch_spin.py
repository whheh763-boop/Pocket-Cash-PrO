import re

with open('app/src/main/java/com/example/ui/screens/SpinWheelScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace("val coins = userState?.coins ?: 0", "val coins = userState.coinBalance")

import_shadow = "import androidx.compose.ui.draw.shadow\n"
if "import androidx.compose.ui.draw.rotate" in content and "import androidx.compose.ui.draw.shadow" not in content:
    content = content.replace("import androidx.compose.ui.draw.rotate\n", "import androidx.compose.ui.draw.rotate\n" + import_shadow)

with open('app/src/main/java/com/example/ui/screens/SpinWheelScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)
