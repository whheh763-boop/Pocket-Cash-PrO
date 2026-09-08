import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

new_funcs = """
    fun updateAppConfig(config: AppConfig) {
        viewModelScope.launch {
            repository.updateAppConfig(config)
        }
    }
"""

content = content.replace("fun addCoins(amount: Int, reason: String = \"Task Reward\") {", new_funcs + "\n    fun addCoins(amount: Int, reason: String = \"Task Reward\") {")

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
