import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

old_func = """    fun performCheckIn(uid: String, rewardAmount: Int = 10) {
        db.runTransaction { transaction ->
            val docRef = usersRef.document(uid)
            val snapshot = transaction.get(docRef)
            val currentCoins = snapshot.getLong("coinBalance") ?: 0
            val lifetime = snapshot.getLong("lifetimeEarnings") ?: 0
            val canCheckIn = snapshot.getBoolean("canCheckIn") ?: false
            
            if (canCheckIn) {
                val currentStreak = snapshot.getLong("currentStreak") ?: 0
                val totalCheckIns = snapshot.getLong("totalCheckIns") ?: 0
                
                transaction.update(docRef, "coinBalance", currentCoins + rewardAmount)
                transaction.update(docRef, "lifetimeEarnings", lifetime + rewardAmount)
                transaction.update(docRef, "canCheckIn", false)
                transaction.update(docRef, "currentStreak", currentStreak + 1)
                transaction.update(docRef, "totalCheckIns", totalCheckIns + 1)
                
                val txRef = docRef.collection("transactions").document()
                val tx = Transaction(
                    id = txRef.id,
                    title = "Daily Check-in",
                    amount = rewardAmount,
                    isCredit = true,
                    status = "Completed",
                    timestamp = System.currentTimeMillis()
                )
                transaction.set(txRef, tx)
            }
        }
    }"""

new_func = """    fun performCheckIn(uid: String) {
        db.runTransaction { transaction ->
            val docRef = usersRef.document(uid)
            val snapshot = transaction.get(docRef)
            val currentCoins = snapshot.getLong("coinBalance") ?: 0
            val lifetime = snapshot.getLong("lifetimeEarnings") ?: 0
            val canCheckIn = snapshot.getBoolean("canCheckIn") ?: false
            
            if (canCheckIn) {
                val currentStreak = (snapshot.getLong("currentStreak") ?: 0).toInt()
                val totalCheckIns = snapshot.getLong("totalCheckIns") ?: 0
                
                // Calculate dynamic reward based on 7-day streak
                val dayIndex = currentStreak % 7
                val rewards = listOf(10, 20, 30, 40, 50, 60, 200)
                val rewardAmount = rewards[dayIndex]
                
                transaction.update(docRef, "coinBalance", currentCoins + rewardAmount)
                transaction.update(docRef, "lifetimeEarnings", lifetime + rewardAmount)
                transaction.update(docRef, "canCheckIn", false)
                transaction.update(docRef, "currentStreak", currentStreak + 1)
                transaction.update(docRef, "totalCheckIns", totalCheckIns + 1)
                
                val txRef = docRef.collection("transactions").document()
                val tx = Transaction(
                    id = txRef.id,
                    title = "Daily Check-in (Day ${dayIndex + 1})",
                    amount = rewardAmount,
                    isCredit = true,
                    status = "Completed",
                    timestamp = System.currentTimeMillis()
                )
                transaction.set(txRef, tx)
            }
        }
    }"""

content = content.replace(old_func, new_func)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    vm_content = f.read()

vm_content = vm_content.replace(
    "repository.performCheckIn(currentUid, _appConfig.value.dailyCheckInReward)",
    "repository.performCheckIn(currentUid)"
)

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(vm_content)
