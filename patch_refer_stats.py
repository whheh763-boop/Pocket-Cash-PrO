import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

stats_logic = """
    suspend fun getReferralStats(refCode: String): Pair<Int, Int> {
        return try {
            val snapshot = usersRef.whereEqualTo("referredBy", refCode).whereEqualTo("referralMilestoneCompleted", true).get().await()
            val totalInvited = snapshot.documents.size
            val coinsEarned = totalInvited * 50 // 50 coins per successful referral
            Pair(totalInvited, coinsEarned)
        } catch (e: Exception) {
            Pair(0, 0)
        }
    }
}
"""

if "getReferralStats" not in content:
    content = re.sub(r'}\s*$', stats_logic, content)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
