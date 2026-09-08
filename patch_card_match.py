import re

with open('app/src/main/java/com/example/ui/screens/CardMatchScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

import_str = "import androidx.compose.foundation.layout.*"
if import_str in content:
    content = content.replace(import_str, import_str + "\nimport androidx.compose.foundation.BorderStroke")

with open('app/src/main/java/com/example/ui/screens/CardMatchScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)
