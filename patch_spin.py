import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

# Modify the transaction in performSecureSpin
old_spin_update = """                if (wonCoins > 0) {
                    val currentCoins = userSnapshot.getLong("coinBalance") ?: 0
                    val lifetime = userSnapshot.getLong("lifetimeEarnings") ?: 0
                    transaction.update(docRef, "coinBalance", currentCoins + wonCoins)
                    transaction.update(docRef, "lifetimeEarnings", lifetime + wonCoins)
                    
                    val txRef = docRef.collection("transactions").document()
                    val tx = Transaction(
                        id = txRef.id,
                        title = "Spin Reward",
                        amount = wonCoins,
                        isCredit = true,
                        status = "Completed",
                        timestamp = currentTime
                    )
                    transaction.set(txRef, tx)
                }
                
                wonCoins
            }.await()"""

new_spin_update = """                
                val currentSpins = userSnapshot.getLong("totalSpins") ?: 0
                transaction.update(docRef, "totalSpins", currentSpins + 1)
                
                if (wonCoins > 0) {
                    val currentCoins = userSnapshot.getLong("coinBalance") ?: 0
                    val lifetime = userSnapshot.getLong("lifetimeEarnings") ?: 0
                    transaction.update(docRef, "coinBalance", currentCoins + wonCoins)
                    transaction.update(docRef, "lifetimeEarnings", lifetime + wonCoins)
                    
                    val txRef = docRef.collection("transactions").document()
                    val tx = Transaction(
                        id = txRef.id,
                        title = "Spin Reward",
                        amount = wonCoins,
                        isCredit = true,
                        status = "Completed",
                        timestamp = currentTime
                    )
                    transaction.set(txRef, tx)
                }
                
                wonCoins
            }.await()
            
            // Check milestone outside transaction
            val user = usersRef.document(uid).get().await().toObject(User::class.java)
            if (user != null && user.totalSpins == 10 && user.referredBy.isNotEmpty() && !user.referralMilestoneCompleted) {
                triggerReferralMilestone(uid, user.referredBy)
            }
"""

if "totalSpins" not in old_spin_update:
    content = content.replace(old_spin_update, new_spin_update)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
