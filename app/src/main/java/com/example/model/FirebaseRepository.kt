package com.example.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRepository {

    fun getAppConfigFlow(): Flow<AppConfig> = callbackFlow {
        val listener = configRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            
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
            if (snapshot != null && snapshot.exists()) {
                val config = snapshot.toObject(AppConfig::class.java) ?: AppConfig()
                trySend(config)
            } else {
                trySend(AppConfig())
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateAppConfig(config: AppConfig) {
        configRef.set(config).await()
    }

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersRef = db.collection("users")
    private val configRef = db.collection("config").document("appSettings")

    private val shopRef = db.collection("shop_products")

    fun getShopProductsFlow(): Flow<List<ShopProduct>> = callbackFlow {
        val listener = shopRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val products = snapshot.documents.mapNotNull { it.toObject(ShopProduct::class.java) }
                trySend(products)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun addShopProduct(product: ShopProduct) {
        val ref = shopRef.document()
        val newProduct = product.copy(id = ref.id)
        ref.set(newProduct).await()
    }

    suspend fun deleteShopProduct(id: String) {
        shopRef.document(id).delete().await()
    }


    private val quizRef = db.collection("quiz_questions")

    
    private val feedbackRef = db.collection("feedbacks")
    
    suspend fun submitFeedback(feedback: Feedback) {
        val ref = feedbackRef.document()
        val newFeedback = feedback.copy(id = ref.id, timestamp = System.currentTimeMillis())
        ref.set(newFeedback).await()
    }
    
    fun getFeedbackFlow(): Flow<List<Feedback>> = callbackFlow {
        val listener = feedbackRef.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Feedback::class.java) }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun getQuizQuestionsFlow(): Flow<List<QuizQuestion>> = callbackFlow {
        val listener = quizRef.whereEqualTo("isActive", true).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val questions = snapshot.documents.mapNotNull { it.toObject(QuizQuestion::class.java) }
                trySend(questions)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun addQuizQuestion(question: QuizQuestion) {
        val ref = quizRef.document()
        val newQ = question.copy(id = ref.id)
        ref.set(newQ).await()
    }

    suspend fun deleteQuizQuestion(id: String) {
        quizRef.document(id).delete().await()
    }


        suspend fun signUpWithEmail(email: String, pass: String, name: String, country: Country, refCode: String, deviceId: String): String {
        val deviceCheck = usersRef.whereEqualTo("deviceId", deviceId).get().await()
        if (!deviceCheck.isEmpty) {
            throw Exception("This device is already registered with another account.")
        }
        
        // Anti-Fraud: Validate Referral Code exists before auth
        var validatedRefCode = ""
        if (refCode.isNotEmpty()) {
            val refCheck = usersRef.whereEqualTo("referralCode", refCode).get().await()
            if (refCheck.isEmpty) {
                throw Exception("Invalid referral code!")
            } else {
                validatedRefCode = refCode
            }
        }
        
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("Auth failed")
            
            // Create user doc
            val allowedChars = ('A'..'Z') + ('0'..'9')
            val myReferralCode = (1..6).map { allowedChars.random() }.joinToString("")
            
            val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val newUser = User(
                deviceId = deviceId,
                lastResetDate = currentDate,
                uid = uid,
                email = email,
                country = country,
                referredBy = validatedRefCode,
                referralCode = myReferralCode,
                coinBalance = 0, // Delayed referral reward
                lifetimeEarnings = 0
            )
            usersRef.document(uid).set(newUser).await()
            
            uid
        } catch (e: Exception) {
            Log.e("Firebase", "Signup Error", e)
            throw e
        }
    }
    
    suspend fun signInWithEmail(email: String, pass: String): String {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            result.user?.uid ?: throw Exception("Auth failed")
        } catch (e: Exception) {
            Log.e("Firebase", "Signin Error", e)
            throw e
        }
    }
    
    
    suspend fun signInWithGoogle(idToken: String): String {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            
            val uid = result.user?.uid ?: throw Exception("Google Auth failed")
            
            // If it's a new user, create their document
            val isNewUser = result.additionalUserInfo?.isNewUser == true
            if (isNewUser) {
                val email = result.user?.email ?: ""
                val name = result.user?.displayName ?: "User"
                val newUser = User(
                    uid = uid,
                    email = email,
                    displayName = name
                    
                )
                usersRef.document(uid).set(newUser).await()
            }
            uid
        } catch (e: Exception) {
            Log.e("Firebase", "Google Signin Error", e)
            throw e
        }
    }

    
    suspend fun resetPassword(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            Log.e("Firebase", "Reset Password Error", e)
            throw e
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getLeaderboardFlow(): Flow<List<User>> = callbackFlow {
        val listener = usersRef.whereGreaterThan("coinBalance", 0).orderBy("coinBalance", Query.Direction.DESCENDING).limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    fun isUserLoggedIn(): String? {
        return auth.currentUser?.uid
    }

    suspend fun signInAnonymously(): String {
        return try {
            val user = auth.currentUser ?: auth.signInAnonymously().await().user
            user?.uid ?: throw Exception("Auth failed")
        } catch (e: Exception) {
            Log.e("Firebase", "Auth Error", e)
            throw e
        }
    }

    fun getUserFlow(uid: String): Flow<User> = callbackFlow {
        val listener = usersRef.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                if (user != null) {
                    val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                    if (user.lastResetDate != currentDate) {
                        usersRef.document(uid).update(
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
                        )
                        return@addSnapshotListener
                    }
                    trySend(user)
                }
            } else {
                // Should not happen for email login, but just in case
                val myReferralCode = UUID.randomUUID().toString().substring(0, 8).uppercase()
                val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                val newUser = User(uid = uid, referralCode = myReferralCode, lastResetDate = currentDate)
                usersRef.document(uid).set(newUser)
                trySend(newUser)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getTransactionsFlow(uid: String): Flow<List<Transaction>> = callbackFlow {
        val listener = usersRef.document(uid).collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Transaction::class.java) }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }



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

    suspend fun performSecureSpin(uid: String, isAdSpin: Boolean): Result<Int> {
        return try {
            val r = kotlin.random.Random.nextDouble() * 100
            val wonCoins = when {
                r < 25.0 -> 0 // 25% (Slot 1)
                r < 55.0 -> 1 // 30% (Slot 2)
                r < 75.0 -> 2 // 20% (Slot 3)
                r < 90.0 -> 5 // 15% (Slot 4)
                r < 96.0 -> 10 // 6% (Slot 5)
                r < 98.0 -> 0 // 2% (Slot 6)
                r < 99.5 -> 20 // 1.5% (Slot 7)
                else -> 50 // 0.5% (Slot 8)
            }

            val finalCoins = db.runTransaction { transaction ->
                val docRef = usersRef.document(uid)
                val userSnapshot = transaction.get(docRef)
                
                val lastSpinTime = userSnapshot.getLong("lastSpinTime") ?: 0L
                val currentTime = System.currentTimeMillis()
                
                if (currentTime - lastSpinTime < 5000L) {
                    throw Exception("Please wait 5 seconds before spinning again!")
                }
                
                val freeSpins = userSnapshot.getLong("dailyFreeSpinsLeft")?.toInt() ?: 10
                val adSpins = userSnapshot.getLong("dailyAdSpinsLeft")?.toInt() ?: 20
                
                if (isAdSpin) {
                    if (adSpins <= 0) throw Exception("Daily Extra Spins limit reached!")
                    transaction.update(docRef, "dailyAdSpinsLeft", adSpins - 1)
                } else {
                    if (freeSpins <= 0) throw Exception("No Free spins left! Watch an ad to spin.")
                    transaction.update(docRef, "dailyFreeSpinsLeft", freeSpins - 1)
                }
                
                transaction.update(docRef, "lastSpinTime", currentTime)
                
                if (wonCoins > 0) {
                    val currentCoins = userSnapshot.getLong("coinBalance") ?: 0
                    val lifetime = userSnapshot.getLong("lifetimeEarnings") ?: 0
                    transaction.update(docRef, "coinBalance", currentCoins + wonCoins)
                    transaction.update(docRef, "lifetimeEarnings", lifetime + wonCoins)
                    
                    val txRef = docRef.collection("transactions").document()
                    val tx = Transaction(
                        id = txRef.id,
                        title = "Spin & Win Reward",
                        amount = wonCoins,
                        isCredit = true,
                        status = "Completed",
                        timestamp = currentTime
                    )
                    transaction.set(txRef, tx)
                }
                
                wonCoins
            }.await()
            Result.success(finalCoins!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun startTask(uid: String, taskId: String) {
        val taskRef = usersRef.document(uid).collection("pendingTasks").document(taskId)
        taskRef.set(mapOf("startTime" to System.currentTimeMillis()))
    }

    suspend fun verifyAndAddCoins(uid: String, taskId: String, amount: Int, reason: String, minDurationMillis: Long): Boolean {
        var success = false
        try {
            db.runTransaction { transaction ->
                val docRef = usersRef.document(uid)
                val taskRef = docRef.collection("pendingTasks").document(taskId)
                
                val taskSnapshot = transaction.get(taskRef)
                if (!taskSnapshot.exists()) {
                    throw Exception("Task not started or already claimed")
                }
                
                val startTime = taskSnapshot.getLong("startTime") ?: 0L
                val currentTime = System.currentTimeMillis()
                
                if (currentTime - startTime < minDurationMillis) {
                    throw Exception("Task completed too quickly (Fraud detection)")
                }
                
                // Add coins
                val userSnapshot = transaction.get(docRef)
                val currentCoins = userSnapshot.getLong("coinBalance") ?: 0
                val lifetime = userSnapshot.getLong("lifetimeEarnings") ?: 0
                
                transaction.update(docRef, "coinBalance", currentCoins + amount)
                transaction.update(docRef, "lifetimeEarnings", lifetime + amount)
                
                // Add tx record
                val txRef = docRef.collection("transactions").document()
                val tx = Transaction(
                    id = txRef.id,
                    title = reason,
                    amount = amount,
                    isCredit = true,
                    status = "Completed",
                    timestamp = currentTime
                )
                transaction.set(txRef, tx)
                
                // Delete the pending task so it can't be reused
                transaction.delete(taskRef)
                success = true
            }.await()
        } catch (e: Exception) {
            Log.e("Firebase", "Fraud Detection: ${e.message}")
            success = false
        }
        return success
    }

    fun addCoins(uid: String, amount: Int, reason: String) {
        db.runTransaction { transaction ->
            val docRef = usersRef.document(uid)
            val snapshot = transaction.get(docRef)
            val currentCoins = snapshot.getLong("coinBalance") ?: 0
            val lifetime = snapshot.getLong("lifetimeEarnings") ?: 0
            
            transaction.update(docRef, "coinBalance", currentCoins + amount)
            transaction.update(docRef, "lifetimeEarnings", lifetime + amount)
            
            // Add transaction record
            val txRef = docRef.collection("transactions").document()
            val tx = Transaction(
                id = txRef.id,
                title = reason,
                amount = amount,
                isCredit = true,
                status = "Completed",
                timestamp = System.currentTimeMillis()
            )
            transaction.set(txRef, tx)
        }
    }
    
    fun performCheckIn(uid: String) {
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
    }

    fun updateProfile(uid: String, name: String, paymentId: String) {
        usersRef.document(uid).update(
            "displayName", name,
            "paymentId", paymentId
        )
    }
    
    fun updateCountry(uid: String, country: Country) {
        usersRef.document(uid).update("country", country.name)
    }
    
    fun useMathAttempt(uid: String): Boolean {
        var success = false
        db.runTransaction { transaction ->
            val docRef = usersRef.document(uid)
            val snapshot = transaction.get(docRef)
            val limits = snapshot.getLong("dailyMathLimit") ?: 0
            if (limits > 0) {
                transaction.update(docRef, "dailyMathLimit", limits - 1)
                success = true
            }
        }
        return success
    }
    
    fun useCaptchaAttempt(uid: String): Boolean {
        var success = false
        db.runTransaction { transaction ->
            val docRef = usersRef.document(uid)
            val snapshot = transaction.get(docRef)
            val limits = snapshot.getLong("dailyCaptchaLimit") ?: 0
            if (limits > 0) {
                transaction.update(docRef, "dailyCaptchaLimit", limits - 1)
                success = true
            }
        }
        return success
    }

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
