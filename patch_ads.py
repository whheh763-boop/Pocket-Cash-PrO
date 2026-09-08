import os

with open('app/src/main/java/com/example/ads/AdsManager.kt', 'r') as f:
    content = f.read()

replacement = """
object AdsManager {
    private const val TAG = "AdsManager"
    
    // Real IDs
    private const val REAL_REWARDED_ID = "ca-app-pub-8551073579787342/1051396163"
    private const val REAL_APP_OPEN_ID = "ca-app-pub-8551073579787342/9590407330"
    private const val REAL_BANNER_ID = "ca-app-pub-8551073579787342/9889790237"
    
    // Test IDs
    private const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
    private const val TEST_APP_OPEN_ID = "ca-app-pub-3940256099942544/9257395921"
    private const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    
    private const val UNITY_GAME_ID = "5996901"
    
    var isRealAdsEnabled = false
    
    private val currentRewardedId: String
        get() = if (isRealAdsEnabled) REAL_REWARDED_ID else TEST_REWARDED_ID
        
    private val currentAppOpenId: String
        get() = if (isRealAdsEnabled) REAL_APP_OPEN_ID else TEST_APP_OPEN_ID
        
    val currentBannerId: String
        get() = if (isRealAdsEnabled) REAL_BANNER_ID else TEST_BANNER_ID

    private var admobRewardedAd: RewardedAd? = null
"""

content = content.replace("""object AdsManager {
    private const val TAG = "AdsManager"
    
    // Using Test IDs, replace with Real IDs on production
    private const val ADMOB_REWARDED_ID = "ca-app-pub-8551073579787342/1051396163"
    private const val ADMOB_APP_OPEN_ID = "ca-app-pub-8551073579787342/9590407330"
    private const val ADMOB_BANNER_ID = "ca-app-pub-8551073579787342/9889790237"
    
    private const val UNITY_GAME_ID = "5996901"
    private var admobRewardedAd: RewardedAd? = null""", replacement)

content = content.replace("ADMOB_REWARDED_ID", "currentRewardedId")
content = content.replace("ADMOB_APP_OPEN_ID", "currentAppOpenId")

banner_replacement = """// Compose wrapper for Banner Ads
@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AdsManager.currentBannerId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}"""

content = content.replace("""// Compose wrapper for Banner Ads
@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-8551073579787342/9889790237" // Test ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}""", banner_replacement)

with open('app/src/main/java/com/example/ads/AdsManager.kt', 'w') as f:
    f.write(content)
