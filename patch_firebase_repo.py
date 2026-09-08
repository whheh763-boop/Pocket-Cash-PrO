import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

feedback_code = """
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
"""

content = content.replace('fun getQuizQuestionsFlow(): Flow<List<QuizQuestion>> = callbackFlow {', feedback_code + '\n    fun getQuizQuestionsFlow(): Flow<List<QuizQuestion>> = callbackFlow {')

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
