import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

# Add triggerReferralMilestone at the end of the class
milestone_logic = """
    suspend fun triggerReferralMilestone(refereeUid: String, refCode: String) {
        try {
            val referrerSnapshot = usersRef.whereEqualTo("referralCode", refCode).get().await()
            if (referrerSnapshot.isEmpty) return
            
            val referrerDoc = referrerSnapshot.documents[0]
            val referrerUid = referrerDoc.id
            
            db.runTransaction { transaction ->
                val refereeRef = usersRef.document(refereeUid)
                val referrerRef = usersRef.document(referrerUid)
                
                val refereeSnap = transaction.get(refereeRef)
                val referrerSnap = transaction.get(referrerRef)
                
                val alreadyCompleted = refereeSnap.getBoolean("referralMilestoneCompleted") ?: false
                if (alreadyCompleted) return@runTransaction
                
                // Referee Reward (20 Coins)
                val currentRefereeCoins = refereeSnap.getLong("coinBalance") ?: 0
                val refereeLifetime = refereeSnap.getLong("lifetimeEarnings") ?: 0
                transaction.update(refereeRef, 
                    "coinBalance", currentRefereeCoins + 20,
                    "lifetimeEarnings", refereeLifetime + 20,
                    "referralMilestoneCompleted", true
                )
                
                val refereeTxRef = refereeRef.collection("transactions").document()
                transaction.set(refereeTxRef, Transaction(
                    id = refereeTxRef.id, title = "Referral Milestone Bonus", amount = 20, isCredit = true, timestamp = System.currentTimeMillis()
                ))
                
                // Referrer Reward (50 Coins)
                val currentReferrerCoins = referrerSnap.getLong("coinBalance") ?: 0
                val referrerLifetime = referrerSnap.getLong("lifetimeEarnings") ?: 0
                transaction.update(referrerRef,
                    "coinBalance", currentReferrerCoins + 50,
                    "lifetimeEarnings", referrerLifetime + 50
                )
                
                val referrerTxRef = referrerRef.collection("transactions").document()
                transaction.set(referrerTxRef, Transaction(
                    id = referrerTxRef.id, title = "Referral Reward (Milestone Met)", amount = 50, isCredit = true, timestamp = System.currentTimeMillis()
                ))
            }.await()
        } catch (e: Exception) {
            Log.e("Firebase", "Milestone Error", e)
        }
    }
}
"""

if "triggerReferralMilestone" not in content:
    content = content.replace("}\n", milestone_logic, 1)  # this might replace the wrong closing brace. I'll use regex.
    content = re.sub(r'}\s*$', milestone_logic, content)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
