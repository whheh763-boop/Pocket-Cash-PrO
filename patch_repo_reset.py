with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

func = """
    suspend fun resetPassword(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            Log.e("Firebase", "Reset Password Error", e)
            throw e
        }
    }
"""

if "fun resetPassword" not in content:
    content = content.replace("fun logout()", func + "\n    fun logout()")

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
