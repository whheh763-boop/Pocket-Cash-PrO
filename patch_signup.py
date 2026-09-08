import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

replacement = """        return try {
            val configSnapshot = db.collection("config").document("appSettings").get().await()
            val config = configSnapshot.toObject(AppConfig::class.java) ?: AppConfig()

            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("Auth failed")
            
            // Create user doc
            val myReferralCode = UUID.randomUUID().toString().substring(0, 8).uppercase()
            val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val newUser = User(
                deviceId = deviceId,
                lastResetDate = currentDate,
                uid = uid,
                email = email,
                country = country,
                referredBy = refCode,
                referralCode = myReferralCode,
                coinBalance = if (refCode.isNotEmpty()) config.signupBonus else 0,
                lifetimeEarnings = if (refCode.isNotEmpty()) config.signupBonus else 0
            )
            usersRef.document(uid).set(newUser).await()
            
            // If they used a code, reward the referrer
            if (refCode.isNotEmpty()) {
                val referrerSnapshot = usersRef.whereEqualTo("referralCode", refCode).get().await()
                for (doc in referrerSnapshot.documents) {
                    val rUid = doc.id
                    val rCoins = doc.getLong("coinBalance") ?: 0
                    val rLifetime = doc.getLong("lifetimeEarnings") ?: 0
                    usersRef.document(rUid).update(
                        "coinBalance", rCoins + config.referralBonus,
                        "lifetimeEarnings", rLifetime + config.referralBonus
                    )
                }
            }"""

content = re.sub(r'        return try \{[\s\S]*?if \(refCode\.isNotEmpty\(\)\) \{[\s\S]*?val referrerSnapshot[\s\S]*?for \(doc in referrerSnapshot\.documents\) \{[\s\S]*?val rUid = doc\.id[\s\S]*?val rCoins = doc\.getLong\("coinBalance"\) \?: 0[\s\S]*?val rLifetime = doc\.getLong\("lifetimeEarnings"\) \?: 0[\s\S]*?usersRef\.document\(rUid\)\.update\([\s\S]*?"coinBalance", rCoins \+ 100,[\s\S]*?"lifetimeEarnings", rLifetime \+ 100[\s\S]*?\)[\s\S]*?\}[\s\S]*?\}', replacement, content)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
