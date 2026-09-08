with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("import Toast", "import android.widget.Toast")

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'w') as f:
    f.write(content)
