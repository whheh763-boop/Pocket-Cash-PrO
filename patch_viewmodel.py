import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

feedback_flow_def = """
    private val _feedbacks = MutableStateFlow<List<Feedback>>(emptyList())
    val feedbacks: StateFlow<List<Feedback>> = _feedbacks.asStateFlow()
"""

# Insert _feedbacks near _leaderboard
content = re.sub(r'(private val _leaderboard = MutableStateFlow<List<User>>\(emptyList\(\)\)\s*val leaderboard: StateFlow<List<User>> = _leaderboard.asStateFlow\(\))', r'\1' + '\n' + feedback_flow_def, content)

submit_feedback_def = """
    fun submitFeedback(name: String, phone: String, reason: String, message: String) {
        viewModelScope.launch {
            try {
                repository.submitFeedback(
                    Feedback(
                        userId = currentUid,
                        name = name,
                        phone = phone,
                        reason = reason,
                        message = message
                    )
                )
            } catch (e: Exception) {
                // Ignore for now
            }
        }
    }
    
    fun observeFeedbacks() {
        viewModelScope.launch {
            repository.getFeedbackFlow().collect { list ->
                _feedbacks.value = list
            }
        }
    }
"""

content = re.sub(r'(fun startObserving\(uid: String\) \{)', submit_feedback_def + r'\n    \1', content)

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
