with open('app/src/main/java/com/example/ui/screens/ReferScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("fun shareReferralCode(context: Context, code: String) {", "fun shareReferralCode(context: Context, code: String, signupBonus: Int) {")
content = content.replace("shareReferralCode(context, userState.referralCode)", "shareReferralCode(context, userState.referralCode, appConfig.signupBonus)")
content = content.replace("${appConfig.signupBonus}", "${signupBonus}")

with open('app/src/main/java/com/example/ui/screens/ReferScreen.kt', 'w') as f:
    f.write(content)
