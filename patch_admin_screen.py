import re

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('fun AdminPanelScreen(viewModel: MainViewModel, onBack: () -> Unit) {', 'fun AdminPanelScreen(viewModel: MainViewModel, onBack: () -> Unit, onNavigateToFeedbacks: () -> Unit = {}) {')

btn_feedbacks = """
            Button(
                onClick = onNavigateToFeedbacks,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text("View User Feedbacks")
            }
"""

content = content.replace('Text("App Configurations", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))', btn_feedbacks + '\nText("App Configurations", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))')

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'w') as f:
    f.write(content)
