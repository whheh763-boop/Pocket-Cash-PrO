import re

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'r') as f:
    content = f.read()

old_sig = """fun ProfileScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {"""

new_sig = """fun ProfileScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToFeedback: () -> Unit = {},
    onLogout: () -> Unit
) {"""

content = content.replace(old_sig, new_sig)

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)
