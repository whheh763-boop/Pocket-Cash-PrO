import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

imports = """
import com.example.ui.screens.AdminFeedbacksScreen
"""

content = content.replace('import com.example.ui.screens.AdminPanelScreen', imports + '\nimport com.example.ui.screens.AdminPanelScreen')

new_routes = """
                composable("admin_feedbacks") {
                    AdminFeedbacksScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
"""

content = content.replace('composable("admin_panel") {', new_routes + '\n                composable("admin_panel") {')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
