import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

content = content.replace("fun performCheckIn(uid: String) {", "fun performCheckIn(uid: String, rewardAmount: Int = 10) {")
content = content.replace("transaction.update(docRef, \"coinBalance\", currentCoins + 10)", "transaction.update(docRef, \"coinBalance\", currentCoins + rewardAmount)")
content = content.replace("transaction.update(docRef, \"lifetimeEarnings\", lifetime + 10)", "transaction.update(docRef, \"lifetimeEarnings\", lifetime + rewardAmount)")
content = content.replace("val tx = Transaction(\n                    id = txRef.id,\n                    title = \"Daily Check-in\",\n                    amount = 10", "val tx = Transaction(\n                    id = txRef.id,\n                    title = \"Daily Check-in\",\n                    amount = rewardAmount")

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
