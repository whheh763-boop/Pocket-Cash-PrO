import re

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'r') as f:
    content = f.read()

sig_new = """
fun ProfileScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToFeedback: () -> Unit = {},
    onLogout: () -> Unit
) {
"""
content = re.sub(r'fun ProfileScreen\([^)]*\)\s*\{', sig_new.strip() + " {", content)

content = content.replace('onClick = { Toast.makeText(context, "Privacy Policy", Toast.LENGTH_SHORT).show() }', 'onClick = onNavigateToPrivacy')

feedback_click = """
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply { data = Uri.parse("mailto:support@pocketcash.com") }
                        context.startActivity(intent)
                    }
"""
content = content.replace(feedback_click, '\n                    onClick = onNavigateToFeedback\n')

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)
