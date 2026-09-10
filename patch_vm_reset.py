with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

func = """
    suspend fun sendPasswordReset(email: String) {
        repository.resetPassword(email)
    }
"""

if "fun sendPasswordReset" not in content:
    content = content.replace("fun logout()", func + "\n    fun logout()")

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
