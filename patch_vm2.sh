sed -i '/fun checkAndResetLimits/i \
    fun updateAppConfig(config: AppConfig, onSuccess: () -> Unit, onError: (String) -> Unit) {\
        viewModelScope.launch(Dispatchers.IO) {\
            try {\
                repository.updateAppConfig(config)\
                withContext(Dispatchers.Main) { onSuccess() }\
            } catch (e: Exception) {\
                withContext(Dispatchers.Main) { onError(e.message ?: "Failed to update config") }\
            }\
        }\
    }\
' app/src/main/java/com/example/viewmodel/MainViewModel.kt
