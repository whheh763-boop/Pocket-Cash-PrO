import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

imports = """
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.FeedbackScreen
"""

content = content.replace('import com.example.ui.screens.AuthScreen', imports + '\nimport com.example.ui.screens.AuthScreen')

new_routes = """
                composable("privacy_policy") {
                    PrivacyPolicyScreen(
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                composable("feedback") {
                    FeedbackScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
"""

content = content.replace('composable("daily_rewards") {', new_routes + '\n                composable("daily_rewards") {')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
