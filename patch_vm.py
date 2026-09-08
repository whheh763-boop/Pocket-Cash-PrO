import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

# Anti-fraud message fix
content = content.replace(
    'onError("Verification failed. Invalid or fraudulent attempt.")',
    'onError("Aapne bahut jaldi jawab diya! Kripya thoda wait karke submit karein taaki ad load ho sake.")'
)

# Observe config to update AdsManager
if "AdsManager.isRealAdsEnabled =" not in content:
    content = content.replace(
        "val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()",
        """val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()

    init {
        viewModelScope.launch {
            _appConfig.collect { config ->
                com.example.ads.AdsManager.isRealAdsEnabled = config.useRealAds
            }
        }
    }"""
    )

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)

