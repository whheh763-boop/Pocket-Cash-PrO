with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

# Fix the brace
content = content.replace("    }\n}\n    suspend fun markQuizCompleted", "    }\n    suspend fun markQuizCompleted")
content = content + "\n}"

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
