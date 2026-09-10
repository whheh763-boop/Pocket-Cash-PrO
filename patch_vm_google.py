import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

google_func = """
    suspend fun loginWithGoogle(idToken: String) {
        val uid = repository.signInWithGoogle(idToken)
        startObserving(uid)
    }
"""

if "fun loginWithGoogle" not in content:
    content = content.replace("fun logout()", google_func + "\n    fun logout()")

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
