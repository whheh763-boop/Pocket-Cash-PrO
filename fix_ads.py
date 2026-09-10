with open('app/src/main/java/com/example/ui/screens/ForgotPasswordScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("import com.example.ui.AdManager", "import com.example.ads.AdsManager\nimport android.app.Activity")

ad_code_old = "AdManager.showInterstitial(context)"
ad_code_new = """val activity = context as? Activity
                                                if (activity != null) {
                                                    AdsManager.showInterstitialAd(activity) {
                                                        // Callback
                                                    }
                                                }"""

content = content.replace(ad_code_old, ad_code_new)

with open('app/src/main/java/com/example/ui/screens/ForgotPasswordScreen.kt', 'w') as f:
    f.write(content)
