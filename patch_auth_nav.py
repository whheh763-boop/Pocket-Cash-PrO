import re

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    "fun AuthScreen(\n    viewModel: MainViewModel,\n    onNavigateToHome: () -> Unit",
    "fun AuthScreen(\n    viewModel: MainViewModel,\n    onNavigateToHome: () -> Unit,\n    onNavigateToForgotPassword: () -> Unit = {}"
)

content = content.replace(
    'Text(\n                                "Forgot Password?", \n                                fontSize = 12.sp, \n                                color = Color(0xFF38BDF8),\n                                modifier = Modifier.clickable { }',
    'Text(\n                                "Forgot Password?", \n                                fontSize = 12.sp, \n                                color = Color(0xFF38BDF8),\n                                modifier = Modifier.clickable { onNavigateToForgotPassword() }'
)

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'w') as f:
    f.write(content)
