import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

scratch_methods = """
    fun generateSecureScratchCard(isAdScratch: Boolean, onResult: (Result<Pair<String, Int>>) -> Unit) {
        if (currentUid.isEmpty()) {
            onResult(Result.failure(Exception("Not logged in")))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.generateSecureScratchCard(currentUid, isAdScratch)
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }

    fun claimSecureScratchReward(taskId: String, onResult: (Result<Int>) -> Unit) {
        if (currentUid.isEmpty()) {
            onResult(Result.failure(Exception("Not logged in")))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.claimSecureScratchReward(currentUid, taskId)
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }
"""

if "generateSecureScratchCard" not in content:
    content = content.replace("    fun performSecureSpin", scratch_methods + "\n    fun performSecureSpin")

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
