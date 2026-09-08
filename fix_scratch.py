import re

with open('app/src/main/java/com/example/ui/screens/ScratchCardScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

content = re.sub(
    r"AnimatedVisibility\(\s*visible = !isScratched,\s*exit = fadeOut\(tween\(500\)\),\s*modifier = Modifier.matchParentSize\(\)\s*\)\s*\{",
    r"if (!isScratched) { Box(modifier = Modifier.matchParentSize()) {",
    content
)

# close the box
content = content.replace("                            }\n                        }\n                    } else {", "                            }\n                        } }\n                    } else {")

with open('app/src/main/java/com/example/ui/screens/ScratchCardScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)

