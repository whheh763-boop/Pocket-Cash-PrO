import re

with open('app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

# Replace Leaderboard label
content = content.replace('label = { Text("Leaderboard") }', 'label = { Text("Leaders", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, softWrap = false) }')

# Replace other labels just in case
content = content.replace('label = { Text("Home") }', 'label = { Text("Home", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, softWrap = false) }')
content = content.replace('label = { Text("Withdraw") }', 'label = { Text("Withdraw", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, softWrap = false) }')
content = content.replace('label = { Text("Refer") }', 'label = { Text("Refer", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, softWrap = false) }')
content = content.replace('label = { Text("Profile") }', 'label = { Text("Profile", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, softWrap = false) }')

with open('app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
