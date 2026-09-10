import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

quiz_methods = """
    fun generateSecureQuiz(type: String, isAd: Boolean, onResult: (Result<com.example.model.SecureQuizData>) -> Unit) {
        if (currentUid.isEmpty()) {
            onResult(Result.failure(Exception("Not logged in")))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.generateSecureQuiz(currentUid, type, isAd)
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }

    fun submitSecureQuizAnswer(taskId: String, selectedIndex: Int, onResult: (Result<Boolean>) -> Unit) {
        if (currentUid.isEmpty()) {
            onResult(Result.failure(Exception("Not logged in")))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.submitSecureQuizAnswer(currentUid, taskId, selectedIndex)
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }
"""

if "generateSecureQuiz" not in content:
    content = content.replace("    fun generateSecureScratchCard", quiz_methods + "\n    fun generateSecureScratchCard")

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
