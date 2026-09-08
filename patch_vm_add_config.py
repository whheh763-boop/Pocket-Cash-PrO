import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

replacement = """
    fun updateAppConfig(config: AppConfig, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateAppConfig(config)
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e.message ?: "Failed to update config") }
            }
        }
    }
}"""

content = re.sub(r'\}$', replacement, content.strip())

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content + "\n")
