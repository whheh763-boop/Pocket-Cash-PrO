import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

# Fix double braces
content = content.replace("            throw e\n        }\n    }\n    }", "            throw e\n        }\n    }")

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
