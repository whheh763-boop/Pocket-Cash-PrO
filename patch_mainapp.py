with open('app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("onLogout = {", "onNavigateToAdmin = { rootNavController.navigate(\"admin_panel\") },\n                    onLogout = {")

with open('app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
