import re

with open('app/src/main/java/com/example/ui/screens/ReferScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("shareReferralCode(context, refCode)", "shareReferralCode(context, refCode, appConfig.signupBonus)")

with open('app/src/main/java/com/example/ui/screens/ReferScreen.kt', 'w') as f:
    f.write(content)
