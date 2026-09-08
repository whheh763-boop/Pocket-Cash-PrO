with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

bad_str = """        return success
    }
}
    suspend fun markQuizCompleted"""
good_str = """        return success
    }

    suspend fun markQuizCompleted"""

content = content.replace(bad_str, good_str)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/model/Models.kt', 'r') as f:
    models = f.read()
models = models.replace("= emptyList()\n))", "= emptyList()\n)")
with open('app/src/main/java/com/example/model/Models.kt', 'w') as f:
    f.write(models)
