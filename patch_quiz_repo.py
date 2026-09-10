import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

# Update Reset logic
old_reset = """                        usersRef.document(uid).update(
                            "dailyMathLimit", 15,
                            "dailyCaptchaLimit", 20,
                            "dailyFreeSpinsLeft", 10,
                            "dailyAdSpinsLeft", 20,
                            "dailyFreeScratchLeft", 10,
                            "dailyAdScratchLeft", 20,
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
                            "dailyFreeMathQuizLeft", 10,
                            "dailyAdMathQuizLeft", 15,
                            "dailyFreeGkQuizLeft", 10,
                            "dailyAdGkQuizLeft", 15,
                            "canCheckIn", true,
                            "lastResetDate", currentDate
                        )"""
content = content.replace(old_reset, new_reset)

# Add Quiz logic
quiz_logic = """
    suspend fun generateSecureQuiz(uid: String, type: String, isAd: Boolean): Result<SecureQuizData> {
        return try {
            val taskId = java.util.UUID.randomUUID().toString()
            
            var questionText = ""
            var options = listOf<String>()
            var correctIndex = 0
            
            if (type == "MATH") {
                val num1 = (10..50).random()
                val num2 = (1..20).random()
                val ops = listOf("+", "-", "*")
                val op = ops.random()
                
                val ans = when (op) {
                    "+" -> num1 + num2
                    "-" -> num1 - num2
                    else -> num1 * num2
                }
                
                questionText = "$num1 $op $num2 = ?"
                val fake1 = ans + (1..10).random()
                val fake2 = ans - (1..10).random()
                val fake3 = ans + (11..20).random()
                
                val rawOptions = mutableListOf(ans.toString(), fake1.toString(), fake2.toString(), fake3.toString())
                rawOptions.shuffle()
                
                correctIndex = rawOptions.indexOf(ans.toString())
                options = rawOptions
            } else {
                // GK fallback
                val gkPool = listOf(
                    Pair("What is the capital of India?", listOf("New Delhi", "Mumbai", "Kolkata", "Chennai")),
                    Pair("Which planet is known as the Red Planet?", listOf("Mars", "Venus", "Jupiter", "Saturn")),
                    Pair("Who wrote the national anthem of India?", listOf("Rabindranath Tagore", "Bankim Chandra", "Subhas Chandra Bose", "Mahatma Gandhi")),
                    Pair("What is the largest ocean on Earth?", listOf("Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean")),
                    Pair("Which is the smallest continent by land area?", listOf("Australia", "Europe", "Antarctica", "South America"))
                )
                
                val q = gkPool.random()
                questionText = q.first
                val rawOptions = q.second.toMutableList()
                val correctAns = rawOptions[0] // Assuming first is correct in the pool
                rawOptions.shuffle()
                correctIndex = rawOptions.indexOf(correctAns)
                options = rawOptions
            }

            val success = db.runTransaction { transaction ->
                val docRef = usersRef.document(uid)
                val userSnapshot = transaction.get(docRef)
                
                if (type == "MATH") {
                    val freeLeft = userSnapshot.getLong("dailyFreeMathQuizLeft")?.toInt() ?: 10
                    val adLeft = userSnapshot.getLong("dailyAdMathQuizLeft")?.toInt() ?: 15
                    if (isAd) {
                        if (adLeft <= 0) throw Exception("Math Ad limits reached!")
                        transaction.update(docRef, "dailyAdMathQuizLeft", adLeft - 1)
                    } else {
                        if (freeLeft <= 0) throw Exception("Math Free limits reached!")
                        transaction.update(docRef, "dailyFreeMathQuizLeft", freeLeft - 1)
                    }
                } else {
                    val freeLeft = userSnapshot.getLong("dailyFreeGkQuizLeft")?.toInt() ?: 10
                    val adLeft = userSnapshot.getLong("dailyAdGkQuizLeft")?.toInt() ?: 15
                    if (isAd) {
                        if (adLeft <= 0) throw Exception("GK Ad limits reached!")
                        transaction.update(docRef, "dailyAdGkQuizLeft", adLeft - 1)
                    } else {
                        if (freeLeft <= 0) throw Exception("GK Free limits reached!")
                        transaction.update(docRef, "dailyFreeGkQuizLeft", freeLeft - 1)
                    }
                }
                
                val taskRef = docRef.collection("pendingQuizzes").document(taskId)
                taskRef.set(mapOf(
                    "correctIndex" to correctIndex,
                    "startTime" to System.currentTimeMillis()
                ))
                
                true
            }.await()
            
            Result.success(SecureQuizData(
                id = taskId,
                type = type,
                question = questionText,
                options = options,
                rewardCoins = 2
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitSecureQuizAnswer(uid: String, taskId: String, selectedIndex: Int): Result<Boolean> {
        return try {
            val isCorrect = db.runTransaction { transaction ->
                val docRef = usersRef.document(uid)
                val taskRef = docRef.collection("pendingQuizzes").document(taskId)
                
                val taskSnapshot = transaction.get(taskRef)
                if (!taskSnapshot.exists()) {
                    throw Exception("Invalid quiz session!")
                }
                
                val correctIndex = taskSnapshot.getLong("correctIndex")?.toInt() ?: -1
                val startTime = taskSnapshot.getLong("startTime") ?: 0L
                val currentTime = System.currentTimeMillis()
                
                val timeElapsed = currentTime - startTime
                // Delete task to prevent replay
                transaction.delete(taskRef)
                
                if (timeElapsed < 1000L) {
                    throw Exception("Answered too fast! Bot detected.")
                }
                if (timeElapsed > 18000L) {
                    // Timeout is 15s, giving 3s buffer for network latency
                    return@runTransaction false
                }
                
                if (selectedIndex == correctIndex) {
                    val userSnapshot = transaction.get(docRef)
                    val currentCoins = userSnapshot.getLong("coinBalance") ?: 0
                    val lifetime = userSnapshot.getLong("lifetimeEarnings") ?: 0
                    
                    transaction.update(docRef, "coinBalance", currentCoins + 2)
                    transaction.update(docRef, "lifetimeEarnings", lifetime + 2)
                    
                    val txRef = docRef.collection("transactions").document()
                    val tx = Transaction(
                        id = txRef.id,
                        title = "Quiz Reward",
                        amount = 2,
                        isCredit = true,
                        status = "Completed",
                        timestamp = currentTime
                    )
                    transaction.set(txRef, tx)
                    true
                } else {
                    false
                }
            }.await()
            Result.success(isCorrect)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
"""

if "generateSecureQuiz" not in content:
    content = content.replace("    suspend fun generateSecureScratchCard", quiz_logic + "\n    suspend fun generateSecureScratchCard")

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
