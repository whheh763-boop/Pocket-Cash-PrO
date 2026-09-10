with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("                    val coroutineScope = rememberCoroutineScope()\n                    val context = LocalContext.current", "")

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'w') as f:
    f.write(content)
