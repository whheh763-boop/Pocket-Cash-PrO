import re

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'r') as f:
    content = f.read()

# Fix the import
content = content.replace("import androidx.compose.material.icons.automirrored.filled.*\npackage com.example.ui.screens\n", "package com.example.ui.screens\nimport androidx.compose.material.icons.automirrored.filled.*\n")

# Check what happened to IconButtonGlass
with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)
