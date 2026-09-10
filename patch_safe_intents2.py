import re
import os

def make_private(filepath):
    if not os.path.exists(filepath):
        return
    with open(filepath, 'r') as f:
        content = f.read()

    content = content.replace("fun safeOpenUrl(context: android.content.Context, url: String)", "private fun safeOpenUrl(context: android.content.Context, url: String)")

    with open(filepath, 'w') as f:
        f.write(content)

make_private('app/src/main/java/com/example/ui/screens/ProfileScreen.kt')
make_private('app/src/main/java/com/example/ui/screens/HomeScreen.kt')
make_private('app/src/main/java/com/example/ui/screens/ScratchScreen.kt')
