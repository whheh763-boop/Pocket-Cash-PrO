import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

# Remove from top
content = re.sub(r'    private val configRef = db\.collection\("config"\)\.document\("appSettings"\)\n', '', content)

# Add below usersRef
content = content.replace("private val usersRef = db.collection(\"users\")", "private val usersRef = db.collection(\"users\")\n    private val configRef = db.collection(\"config\").document(\"appSettings\")")

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
