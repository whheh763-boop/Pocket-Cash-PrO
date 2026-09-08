import re

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("package com.example.ui.screens", "package com.example.ui.screens\n\nimport android.widget.Toast")
content = content.replace("android.widget.Toast", "Toast")

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'w') as f:
    f.write(content)
