import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

quiz_methods = """
    private val quizRef = db.collection("quiz_questions")

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
"""

content = content.replace("private val configRef = db.collection(\"config\").document(\"appSettings\")", "private val configRef = db.collection(\"config\").document(\"appSettings\")\n" + quiz_methods)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
