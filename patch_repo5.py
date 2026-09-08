import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

quiz_completion = """
    suspend fun markQuizCompleted(uid: String, quizId: String, reward: Int) {
        db.runTransaction { transaction ->
            val docRef = usersRef.document(uid)
            val snapshot = transaction.get(docRef)
            val currentCoins = snapshot.getLong("coinBalance") ?: 0
            val lifetime = snapshot.getLong("lifetimeEarnings") ?: 0
            val completedQuizzes = (snapshot.get("completedQuizzes") as? List<String>) ?: emptyList()

            if (!completedQuizzes.contains(quizId)) {
                val newList = completedQuizzes + quizId
                transaction.update(docRef, "coinBalance", currentCoins + reward)
                transaction.update(docRef, "lifetimeEarnings", lifetime + reward)
                transaction.update(docRef, "completedQuizzes", newList)

                val txRef = usersRef.document(uid).collection("transactions").document()
                val tx = Transaction(
                    id = txRef.id,
                    title = "Quiz Reward",
                    amount = reward,
                    isCredit = true,
                    status = "Completed",
                    timestamp = System.currentTimeMillis()
                )
                transaction.set(txRef, tx)
            }
        }.await()
    }
"""

content = content + quiz_completion

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
