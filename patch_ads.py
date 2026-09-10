import re

with open('app/src/main/java/com/example/ads/AdsManager.kt', 'r') as f:
    content = f.read()

import_statement = "import com.google.android.gms.ads.interstitial.InterstitialAd\nimport com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback\n"
content = content.replace("import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback", "import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback\n" + import_statement)

interstitial_code = """
    private const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val REAL_INTERSTITIAL_ID = "ca-app-pub-8551073579787342/1234567890" // Placeholder
    private val currentInterstitialId: String
        get() = if (isRealAdsEnabled) REAL_INTERSTITIAL_ID else TEST_INTERSTITIAL_ID
        
    private var admobInterstitialAd: InterstitialAd? = null

    private fun loadAdMobInterstitial(context: Context) {
        if (admobInterstitialAd != null) return
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, currentInterstitialId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                admobInterstitialAd = ad
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                admobInterstitialAd = null
            }
        })
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
        if (admobInterstitialAd != null) {
            admobInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    admobInterstitialAd = null
                    loadAdMobInterstitial(activity)
                    onAdDismissed()
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    admobInterstitialAd = null
                    loadAdMobInterstitial(activity)
                    onAdDismissed()
                }
            }
            admobInterstitialAd?.show(activity)
        } else {
            loadAdMobInterstitial(activity)
            onAdDismissed()
        }
    }
"""

if "showInterstitialAd" not in content:
    content = content.replace("    // --- App Open Ad ---", interstitial_code + "\n    // --- App Open Ad ---")
    content = content.replace("loadAdMobRewarded(activity)", "loadAdMobRewarded(activity)\n            loadAdMobInterstitial(activity)")

with open('app/src/main/java/com/example/ads/AdsManager.kt', 'w') as f:
    f.write(content)
