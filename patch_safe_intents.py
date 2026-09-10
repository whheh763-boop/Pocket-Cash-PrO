import re
import os

def wrap_intents(filepath):
    if not os.path.exists(filepath):
        return
    with open(filepath, 'r') as f:
        content = f.read()

    # Find context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(...)))
    # We will replace context.startActivity(...) with a safe wrapper function, but since it's inline, we can just replace the specific lines or add an extension function.
    
    # Adding a helper function if not present
    helper = """
fun safeOpenUrl(context: android.content.Context, url: String) {
    try {
        context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)))
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "No app found to open this link", android.widget.Toast.LENGTH_SHORT).show()
    }
}
"""
    if "safeOpenUrl" not in content:
        content += helper

    # Now replace usages
    content = re.sub(r'context\.startActivity\(Intent\(Intent\.ACTION_VIEW,\s*Uri\.parse\((.*?)\)\)\)', r'safeOpenUrl(context, \1)', content)

    with open(filepath, 'w') as f:
        f.write(content)

wrap_intents('app/src/main/java/com/example/ui/screens/ProfileScreen.kt')
wrap_intents('app/src/main/java/com/example/ui/screens/HomeScreen.kt')
wrap_intents('app/src/main/java/com/example/ui/screens/ScratchScreen.kt')
