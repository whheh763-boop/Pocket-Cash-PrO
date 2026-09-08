import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

replacement = """
            if (canCheckIn) {
                val currentStreak = snapshot.getLong("currentStreak") ?: 0
                val totalCheckIns = snapshot.getLong("totalCheckIns") ?: 0
                
                transaction.update(docRef, "coinBalance", currentCoins + rewardAmount)
                transaction.update(docRef, "lifetimeEarnings", lifetime + rewardAmount)
                transaction.update(docRef, "canCheckIn", false)
                transaction.update(docRef, "currentStreak", currentStreak + 1)
                transaction.update(docRef, "totalCheckIns", totalCheckIns + 1)
"""

content = re.sub(r'            if \(canCheckIn\) \{\n                transaction.update\(docRef, "coinBalance", currentCoins \+ rewardAmount\)\n                transaction.update\(docRef, "lifetimeEarnings", lifetime \+ rewardAmount\)\n                transaction.update\(docRef, "canCheckIn", false\)', replacement.strip('\n'), content)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
