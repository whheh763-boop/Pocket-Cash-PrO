import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

quiz_methods = """
    private val _quizQuestions = MutableStateFlow<List<com.example.model.QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<com.example.model.QuizQuestion>> = _quizQuestions.asStateFlow()

    fun addQuizQuestion(q: com.example.model.QuizQuestion, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addQuizQuestion(q)
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e.message ?: "Failed") }
            }
        }
    }
"""

content = content.replace("private val _appConfig = MutableStateFlow(AppConfig())", "private val _appConfig = MutableStateFlow(AppConfig())\n" + quiz_methods)

# Start observing quiz questions in startObserving
start_obs = """        // Load config
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAppConfigFlow().collect {
                _appConfig.value = it
            }
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            repository.getQuizQuestionsFlow().collect {
                _quizQuestions.value = it
            }
        }"""

content = re.sub(r'        // Load config[\s\S]*?\}', start_obs, content)

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
