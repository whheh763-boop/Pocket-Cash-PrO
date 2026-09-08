sed -i 's/import com.example.model.Transaction/import com.example.model.Transaction\nimport com.example.model.AppConfig/' app/src/main/java/com/example/viewmodel/MainViewModel.kt

sed -i '/val leaderboard:/a \
\
    private val _appConfig = MutableStateFlow(AppConfig())\
    val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()\
\
    private var configFlowJob: Job? = null\
' app/src/main/java/com/example/viewmodel/MainViewModel.kt

sed -i '/init {/a \
        configFlowJob = viewModelScope.launch {\
            repository.getAppConfigFlow().collect {\
                _appConfig.value = it\
            }\
        }\
' app/src/main/java/com/example/viewmodel/MainViewModel.kt

