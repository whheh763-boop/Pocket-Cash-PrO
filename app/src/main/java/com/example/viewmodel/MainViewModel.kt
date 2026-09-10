package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Country
import com.example.model.User
import com.example.model.Feedback
import com.example.model.Transaction
import com.example.model.AppConfig
import com.example.model.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job

class MainViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private var currentUid: String = ""
    private val _userState = MutableStateFlow(User())
    val userState: StateFlow<User> = _userState.asStateFlow()
    private val _webViewUrl = MutableStateFlow("")
    val webViewUrl: StateFlow<String> = _webViewUrl.asStateFlow()
    private val _webViewTitle = MutableStateFlow("")
    val webViewTitle: StateFlow<String> = _webViewTitle.asStateFlow()
    
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _leaderboard = MutableStateFlow<List<User>>(emptyList())
    val leaderboard: StateFlow<List<User>> = _leaderboard.asStateFlow()

    private val _feedbacks = MutableStateFlow<List<Feedback>>(emptyList())
    val feedbacks: StateFlow<List<Feedback>> = _feedbacks.asStateFlow()


    private val _appConfig = MutableStateFlow(AppConfig())

    
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

    val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()



    private var configFlowJob: Job? = null

    
    private var userFlowJob: Job? = null
    private var txFlowJob: Job? = null
    private var leaderFlowJob: Job? = null

    init {
        configFlowJob = viewModelScope.launch {
            repository.getAppConfigFlow().collect {
                _appConfig.value = it
                com.example.ads.AdsManager.isRealAdsEnabled = it.useRealAds
            }
        }

        // Automatically check if logged in
        val uid = repository.isUserLoggedIn()
        if (uid != null) {
            startObserving(uid)
        }
    }
    
    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn() != null
    }
    
    suspend fun login(email: String, pass: String) {
        val uid = repository.signInWithEmail(email, pass)
        startObserving(uid)
    }
    
    suspend fun signup(email: String, pass: String, name: String, country: Country, refCode: String, deviceId: String) {
        val uid = repository.signUpWithEmail(email, pass, name, country, refCode, deviceId)
        startObserving(uid)
    }
    
    
    suspend fun loginWithGoogle(idToken: String) {
        val uid = repository.signInWithGoogle(idToken)
        startObserving(uid)
    }

    
    suspend fun sendPasswordReset(email: String) {
        repository.resetPassword(email)
    }

    fun logout() {
        repository.logout()
        currentUid = ""
        userFlowJob?.cancel()
        txFlowJob?.cancel()
        _userState.value = User()
        _transactions.value = emptyList()
    }

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

    fun startObserving(uid: String) {
        currentUid = uid
        userFlowJob?.cancel()
        txFlowJob?.cancel()
        leaderFlowJob?.cancel()
        
        userFlowJob = viewModelScope.launch(Dispatchers.IO) {
            repository.getUserFlow(currentUid).collect { user ->
                _userState.value = user
            }
        }
        
        txFlowJob = viewModelScope.launch(Dispatchers.IO) {
            repository.getTransactionsFlow(currentUid).collect { txList ->
                _transactions.value = txList
            }
        }
        
        leaderFlowJob = viewModelScope.launch(Dispatchers.IO) {
            repository.getLeaderboardFlow().collect { list ->
                _leaderboard.value = list
            }
        }
    }

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun updateProfile(name: String, paymentId: String) {
        if (currentUid.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateProfile(currentUid, name, paymentId)
            }
        }
    }

    fun updateCountry(country: Country) {
        if (currentUid.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateCountry(currentUid, country)
            }
        }
    }

    fun startSecureTask(taskId: String) {
        if (currentUid.isNotEmpty()) {
            repository.startTask(currentUid, taskId)
        }
    }

    fun claimSecureReward(taskId: String, amount: Int, reason: String, minDurationMillis: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (currentUid.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.verifyAndAddCoins(currentUid, taskId, amount, reason, minDurationMillis)
            withContext(Dispatchers.Main) {
                if (success) onSuccess() else onError("Aapne bahut jaldi jawab diya! Kripya thoda wait karke submit karein taaki ad load ho sake.")
            }
        }
    }

    
    fun updateAppConfig(config: AppConfig) {
        viewModelScope.launch {
            repository.updateAppConfig(config)
        }
    }

    fun addCoins(amount: Int, reason: String = "Task Reward") {
        if (currentUid.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.addCoins(currentUid, amount, reason)
            }
        }
    }
    


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

    fun performSecureSpin(isAdSpin: Boolean, onResult: (Result<Int>) -> Unit) {
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

    fun performDailyCheckIn() {
        if (currentUid.isNotEmpty() && _userState.value.canCheckIn) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.performCheckIn(currentUid)
            }
        }
    }

    fun useMathQuizAttempt(onSuccess: () -> Unit, onFail: () -> Unit) {
        if (currentUid.isEmpty() || _userState.value.dailyMathLimit <= 0) {
            onFail()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.useMathAttempt(currentUid)
            withContext(Dispatchers.Main) {
                if (success) {
                    onSuccess()
                } else {
                    onFail()
                }
            }
        }
    }

    fun useCaptchaAttempt(onSuccess: () -> Unit, onFail: () -> Unit) {
        if (currentUid.isEmpty() || _userState.value.dailyCaptchaLimit <= 0) {
            onFail()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.useCaptchaAttempt(currentUid)
            withContext(Dispatchers.Main) {
                if (success) {
                    onSuccess()
                } else {
                    onFail()
                }
            }
        }
    }

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

    private val _referralStats = MutableStateFlow(Pair(0, 0))
    val referralStats: StateFlow<Pair<Int, Int>> = _referralStats.asStateFlow()

    fun fetchReferralStats(refCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stats = repository.getReferralStats(refCode)
            _referralStats.value = stats
        }
    }

}