import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

shop_methods = """
    private val _shopProducts = MutableStateFlow<List<com.example.model.ShopProduct>>(emptyList())
    val shopProducts: StateFlow<List<com.example.model.ShopProduct>> = _shopProducts.asStateFlow()

    fun addShopProduct(p: com.example.model.ShopProduct, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addShopProduct(p)
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e.message ?: "Failed") }
            }
        }
    }
    
    fun deleteShopProduct(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteShopProduct(id)
        }
    }
"""

content = content.replace("private val _quizQuestions = MutableStateFlow<List<com.example.model.QuizQuestion>>(emptyList())", shop_methods + "\n    private val _quizQuestions = MutableStateFlow<List<com.example.model.QuizQuestion>>(emptyList())")

# Add to startObserving
start_obs = """        viewModelScope.launch(Dispatchers.IO) {
            repository.getShopProductsFlow().collect {
                _shopProducts.value = it
            }
        }"""
content = content.replace("repository.getQuizQuestionsFlow().collect {", start_obs + "\n        viewModelScope.launch(Dispatchers.IO) {\n            repository.getQuizQuestionsFlow().collect {")

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
