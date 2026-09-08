import re

with open('app/src/main/java/com/example/ui/screens/MathCaptchaScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("userState.mathQuizLimit", "userState.dailyMathLimit")
content = content.replace("userState.captchaLimit", "userState.dailyCaptchaLimit")

with open('app/src/main/java/com/example/ui/screens/MathCaptchaScreen.kt', 'w') as f:
    f.write(content)
