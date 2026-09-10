import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

# Add import if missing
if "import com.google.firebase.auth.GoogleAuthProvider" not in content:
    content = content.replace("import com.google.firebase.auth.FirebaseAuth", "import com.google.firebase.auth.FirebaseAuth\nimport com.google.firebase.auth.GoogleAuthProvider\nimport com.google.firebase.auth.AuthCredential")

# Add signInWithGoogle
google_func = """
    suspend fun signInWithGoogle(idToken: String): String {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            
            val uid = result.user?.uid ?: throw Exception("Google Auth failed")
            
            // If it's a new user, create their document
            val isNewUser = result.additionalUserInfo?.isNewUser == true
            if (isNewUser) {
                val email = result.user?.email ?: ""
                val name = result.user?.displayName ?: "User"
                val newUser = User(
                    uid = uid,
                    email = email,
                    displayName = name,
                    joinDate = System.currentTimeMillis()
                )
                usersRef.document(uid).set(newUser).await()
            }
            uid
        } catch (e: Exception) {
            Log.e("Firebase", "Google Signin Error", e)
            throw e
        }
    }
"""
if "fun signInWithGoogle" not in content:
    content = content.replace("fun logout()", google_func + "\n    fun logout()")

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
