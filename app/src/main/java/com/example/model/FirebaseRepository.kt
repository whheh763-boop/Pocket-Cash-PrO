package com.example.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
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


    suspend fun signUpWithEmail(email: String, pass: String, country: Country, refCode: String, deviceId: String): String {
        val deviceCheck = usersRef.whereEqualTo("deviceId", deviceId).get().await()
        if (!deviceCheck.isEmpty) {
            throw Exception("This device is already registered with another account.")
        }
        return try {
            val configSnapshot = db.collection("config").document("appSettings").get().await()
            val config = configSnapshot.toObject(AppConfig::class.java) ?: AppConfig()

            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("Auth failed")
            
            // Create user doc
            val myReferralCode = UUID.randomUUID().toString().substring(0, 8).uppercase()
            val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val newUser = User(
                deviceId = deviceId,
                lastResetDate = currentDate,
                uid = uid,
                email = email,
                country = country,
                referredBy = refCode,
                referralCode = myReferralCode,
                coinBalance = if (refCode.isNotEmpty()) config.signupBonus else 0,
                lifetimeEarnings = if (refCode.isNotEmpty()) config.signupBonus else 0
            )
            usersRef.document(uid).set(newUser).await()
            
            // If they used a code, reward the referrer
            if (refCode.isNotEmpty()) {
                val referrerSnapshot = usersRef.whereEqualTo("referralCode", refCode).get().await()
                for (doc in referrerSnapshot.documents) {
                    val rUid = doc.id
                    val rCoins = doc.getLong("coinBalance") ?: 0
                    val rLifetime = doc.getLong("lifetimeEarnings") ?: 0
                    usersRef.document(rUid).update(
                        "coinBalance", rCoins + config.referralBonus,
                        "lifetimeEarnings", rLifetime + config.referralBonus
                    )
                }
            }
            
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
    
    fun performCheckIn(uid: String, rewardAmount: Int = 10) {
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

}