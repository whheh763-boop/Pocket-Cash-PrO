import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

quiz_completion = """
    fun submitQuizAnswer(quizId: String, reward: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (currentUid.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.markQuizCompleted(currentUid, quizId, reward)
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e.message ?: "Failed") }
            }
        }
    }
}
"""
content = re.sub(r'\}$', quiz_completion, content.strip())

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content + "\n")
