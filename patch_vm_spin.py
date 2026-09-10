import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

spin_logic = """    fun performSecureSpin(isAdSpin: Boolean, onResult: (Result<Int>) -> Unit) {
        if (currentUid.isEmpty()) {
            onResult(Result.failure(Exception("Not logged in")))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.performSecureSpin(currentUid, isAdSpin)
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }
"""

if "fun performSecureSpin" not in content:
    content = content.replace("    fun performDailyCheckIn()", spin_logic + "\n    fun performDailyCheckIn()")

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
