with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'r') as f:
    content = f.read()

# First, I will extract the misplaced section
import re

bad_pattern = r'Text\("Quiz Game Controls"[\s\S]*?Button\(onClick = onBack\)'
match = re.search(bad_pattern, content)

if match:
    quiz_code = match.group(0).replace('Button(onClick = onBack)', '')
    content = content.replace(match.group(0), 'Button(onClick = onBack)')
    
    # Now place it right before the Save Configuration Button
    content = content.replace('            Button(\n                onClick = {\n                    isSaving = true', quiz_code + '            Button(\n                onClick = {\n                    isSaving = true')
    
    with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'w') as f:
        f.write(content)
