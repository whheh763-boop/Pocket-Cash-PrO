sed -i '/class FirebaseRepository {/a \
    private val configRef = db.collection("config").document("appSettings")\
\
    fun getAppConfigFlow(): Flow<AppConfig> = callbackFlow {\
        val listener = configRef.addSnapshotListener { snapshot, error ->\
            if (error != null) {\
                close(error)\
                return@addSnapshotListener\
            }\
            if (snapshot != null && snapshot.exists()) {\
                val config = snapshot.toObject(AppConfig::class.java) ?: AppConfig()\
                trySend(config)\
            } else {\
                trySend(AppConfig())\
            }\
        }\
        awaitClose { listener.remove() }\
    }\
\
    suspend fun updateAppConfig(config: AppConfig) {\
        configRef.set(config).await()\
    }\
' app/src/main/java/com/example/model/FirebaseRepository.kt
