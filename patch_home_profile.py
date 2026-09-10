import re

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'r') as f:
    content = f.read()
    
content = content.replace(
    'text = if (userState?.displayName.isNullOrBlank() || userState?.displayName == "Guest User") "PocketCash Pro" else userState!!.displayName,',
    'text = if (userState?.displayName.isNullOrBlank()) "PocketCash Pro" else userState!!.displayName,'
)

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('userState.displayName.ifEmpty { "Guest User" },', 'userState.displayName.ifEmpty { "PocketCash Pro" },')

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)
