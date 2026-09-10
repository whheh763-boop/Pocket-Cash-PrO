import re

with open('app/src/main/java/com/example/ui/screens/ScratchCardScreen.kt', 'r') as f:
    content = f.read()

# Fix 1: toRect()
content = content.replace("drawContext.canvas.saveLayer(size.toRect(), androidx.compose.ui.graphics.Paint())", 
                          "drawContext.canvas.saveLayer(androidx.compose.ui.geometry.Rect(Offset.Zero, size), androidx.compose.ui.graphics.Paint())")


# Fix 2: AnimatedVisibility
old_av = """                        // Scratch Overlay Layer
                        AnimatedVisibility(
                            visible = !isScratched,
                            exit = fadeOut(animationSpec = tween(500))
                        ) {"""

new_av = """                        // Scratch Overlay Layer
                        androidx.compose.animation.AnimatedVisibility(
                            visible = !isScratched,
                            exit = fadeOut(animationSpec = tween(500))
                        ) {"""

content = content.replace(old_av, new_av)


with open('app/src/main/java/com/example/ui/screens/ScratchCardScreen.kt', 'w') as f:
    f.write(content)
