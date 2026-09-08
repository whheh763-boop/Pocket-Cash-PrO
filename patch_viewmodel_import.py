import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace('import com.example.model.User', 'import com.example.model.User\nimport com.example.model.Feedback')
content = content.replace('private fun submitFeedback', 'fun submitFeedback') # Just in case

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/screens/AdminFeedbacksScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('import com.example.viewmodel.MainViewModel', 'import com.example.viewmodel.MainViewModel\nimport com.example.model.Feedback')
with open('app/src/main/java/com/example/ui/screens/AdminFeedbacksScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

content = content.replace('import com.example.model.User', 'import com.example.model.User\nimport com.example.model.Feedback')
with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/screens/PrivacyPolicyScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('padding(left = 8.dp)', 'padding(start = 8.dp)')
with open('app/src/main/java/com/example/ui/screens/PrivacyPolicyScreen.kt', 'w') as f:
    f.write(content)
