import re

with open('app/src/main/java/com/example/ui/screens/ScratchCardScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace("AnimatedVisibility(\\n                            visible = !isScratched,\\n                            exit = fadeOut(tween(500)),\\n                            modifier = Modifier.matchParentSize()\\n                        ) {", "if (!isScratched) { Box(modifier = Modifier.matchParentSize()) {")
content = content.replace("                            }\\n                        }\\n                    } else {", "                            }\\n                        } }\\n                    } else {")

with open('app/src/main/java/com/example/ui/screens/ScratchCardScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)
