import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

new_signup = """    suspend fun signUpWithEmail(email: String, pass: String, country: Country, refCode: String, deviceId: String): String {
        val deviceCheck = usersRef.whereEqualTo("deviceId", deviceId).get().await()
        if (!deviceCheck.isEmpty) {
            throw Exception("This device is already registered with another account.")
        }
        
        // Anti-Fraud: Validate Referral Code exists before auth
        var validatedRefCode = ""
        if (refCode.isNotEmpty()) {
            val refCheck = usersRef.whereEqualTo("referralCode", refCode).get().await()
            if (refCheck.isEmpty) {
                throw Exception("Invalid referral code!")
            } else {
                validatedRefCode = refCode
            }
        }
        
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("Auth failed")
            
            // Create user doc
            val allowedChars = ('A'..'Z') + ('0'..'9')
            val myReferralCode = (1..6).map { allowedChars.random() }.joinToString("")
            
            val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val newUser = User(
                deviceId = deviceId,
                lastResetDate = currentDate,
                uid = uid,
                email = email,
                country = country,
                referredBy = validatedRefCode,
                referralCode = myReferralCode,
                coinBalance = 0, // Delayed referral reward
                lifetimeEarnings = 0
            )
            usersRef.document(uid).set(newUser).await()
            
            uid
        } catch (e: Exception) {
            Log.e("Firebase", "Signup Error", e)
            throw e
        }
    }"""

# Use regex to replace the old function block
content = re.sub(r'suspend fun signUpWithEmail\(.*?\{.*?Log\.e\("Firebase", "Signup Error", e\)\n\s+throw e\n\s+\}', new_signup, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
