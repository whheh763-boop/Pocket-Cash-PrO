import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

# Update Reset logic
old_reset = """                        usersRef.document(uid).update(
                            "dailyMathLimit", 15,
                            "dailyCaptchaLimit", 20,
                            "dailyFreeSpinsLeft", 10,
                            "dailyAdSpinsLeft", 20,
                            "canCheckIn", true,
                            "lastResetDate", currentDate
                        )"""

new_reset = """                        usersRef.document(uid).update(
                            "dailyMathLimit", 15,
                            "dailyCaptchaLimit", 20,
                            "dailyFreeSpinsLeft", 10,
                            "dailyAdSpinsLeft", 20,
                            "dailyFreeScratchLeft", 10,
                            "dailyAdScratchLeft", 20,
                            "canCheckIn", true,
                            "lastResetDate", currentDate
                        )"""
content = content.replace(old_reset, new_reset)

# Add Scratch logic
scratch_logic = """
    suspend fun generateSecureScratchCard(uid: String, isAdScratch: Boolean): Result<Pair<String, Int>> {
        return try {
            val r = kotlin.random.Random.nextDouble() * 100
            val rewardAmount = when {
                r < 25.0 -> 0 // 25% (Slot 1)
                r < 60.0 -> 1 // 35% (Slot 2)
                r < 80.0 -> 2 // 20% (Slot 3)
                r < 92.0 -> 5 // 12% (Slot 4)
                r < 97.0 -> 10 // 5% (Slot 5)
                r < 99.5 -> 25 // 2.5% (Slot 6)
                else -> 50 // 0.5% (Slot 7)
            }

            val taskId = java.util.UUID.randomUUID().toString()

            val success = db.runTransaction { transaction ->
                val docRef = usersRef.document(uid)
                val userSnapshot = transaction.get(docRef)
                
                val freeScratch = userSnapshot.getLong("dailyFreeScratchLeft")?.toInt() ?: 10
                val adScratch = userSnapshot.getLong("dailyAdScratchLeft")?.toInt() ?: 20
                
                if (isAdScratch) {
                    if (adScratch <= 0) throw Exception("Daily Extra Scratch limit reached!")
                    transaction.update(docRef, "dailyAdScratchLeft", adScratch - 1)
                } else {
                    if (freeScratch <= 0) throw Exception("No Free scratches left! Watch an ad to scratch.")
                    transaction.update(docRef, "dailyFreeScratchLeft", freeScratch - 1)
                }
                
                val taskRef = docRef.collection("pendingScratches").document(taskId)
                taskRef.set(mapOf(
                    "reward" to rewardAmount,
                    "startTime" to System.currentTimeMillis()
                ))
                
                true
            }.await()
            Result.success(Pair(taskId, rewardAmount))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun claimSecureScratchReward(uid: String, taskId: String): Result<Int> {
        return try {
            val rewardCoins = db.runTransaction { transaction ->
                val docRef = usersRef.document(uid)
                val taskRef = docRef.collection("pendingScratches").document(taskId)
                
                val taskSnapshot = transaction.get(taskRef)
                if (!taskSnapshot.exists()) {
                    throw Exception("Invalid or already claimed scratch card!")
                }
                
                val reward = taskSnapshot.getLong("reward")?.toInt() ?: 0
                val startTime = taskSnapshot.getLong("startTime") ?: 0L
                val currentTime = System.currentTimeMillis()
                
                if (currentTime - startTime < 2000L) {
                    throw Exception("Scratched too fast! Fraud detection triggered.")
                }
                
                if (reward > 0) {
                    val userSnapshot = transaction.get(docRef)
                    val currentCoins = userSnapshot.getLong("coinBalance") ?: 0
                    val lifetime = userSnapshot.getLong("lifetimeEarnings") ?: 0
                    
                    transaction.update(docRef, "coinBalance", currentCoins + reward)
                    transaction.update(docRef, "lifetimeEarnings", lifetime + reward)
                    
                    val txRef = docRef.collection("transactions").document()
                    val tx = Transaction(
                        id = txRef.id,
                        title = "Scratch & Win Reward",
                        amount = reward,
                        isCredit = true,
                        status = "Completed",
                        timestamp = currentTime
                    )
                    transaction.set(txRef, tx)
                }
                
                transaction.delete(taskRef)
                reward
            }.await()
            Result.success(rewardCoins!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
"""

if "generateSecureScratchCard" not in content:
    content = content.replace("    suspend fun performSecureSpin", scratch_logic + "\n    suspend fun performSecureSpin")

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
