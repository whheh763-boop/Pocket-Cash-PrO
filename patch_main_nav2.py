import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Add import for ForgotPasswordScreen
content = content.replace("import com.example.ui.screens.AuthScreen", "import com.example.ui.screens.AuthScreen\nimport com.example.ui.screens.ForgotPasswordScreen")

# Add navigation to AuthScreen
auth_old = """                    AuthScreen(
                        viewModel = mainViewModel,
                        onNavigateToHome = { rootNavController.navigate("main") { popUpTo("auth") { inclusive = true } } }
                    )"""

auth_new = """                    AuthScreen(
                        viewModel = mainViewModel,
                        onNavigateToHome = { rootNavController.navigate("main") { popUpTo("auth") { inclusive = true } } },
                        onNavigateToForgotPassword = { rootNavController.navigate("forgot_password") }
                    )"""

content = content.replace(auth_old, auth_new)

# Add route for forgot_password
route_new = """                composable("forgot_password") {
                    ForgotPasswordScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                composable("main") {"""

content = content.replace('                composable("main") {', route_new)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
