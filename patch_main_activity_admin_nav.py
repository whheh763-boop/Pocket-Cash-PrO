import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace('composable("admin_panel") {\n                    AdminPanelScreen(\n                        viewModel = mainViewModel,\n                        onBack = { rootNavController.popBackStack() }\n                    )\n                }', 'composable("admin_panel") {\n                    AdminPanelScreen(\n                        viewModel = mainViewModel,\n                        onBack = { rootNavController.popBackStack() },\n                        onNavigateToFeedbacks = { rootNavController.navigate("admin_feedbacks") }\n                    )\n                }')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
