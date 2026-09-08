import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace("""    init {
        viewModelScope.launch {
            _appConfig.collect { config ->
                com.example.ads.AdsManager.isRealAdsEnabled = config.useRealAds
            }
        }
    }""", "")

content = content.replace("""    init {
        configFlowJob = viewModelScope.launch {
            repository.getAppConfigFlow().collect {
                _appConfig.value = it""", """    init {
        configFlowJob = viewModelScope.launch {
            repository.getAppConfigFlow().collect {
                _appConfig.value = it
                com.example.ads.AdsManager.isRealAdsEnabled = it.useRealAds""")

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)

