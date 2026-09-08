import re

with open('app/src/main/java/com/example/ui/screens/MathCaptchaScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("val limits by viewModel.limitsState.collectAsState()", "val userState by viewModel.userState.collectAsState()")
content = content.replace("limits.mathQuizLimit", "userState.mathQuizLimit")
content = content.replace("limits.captchaLimit", "userState.captchaLimit")
content = content.replace("onLimitReached = {", "onFail = {")

with open('app/src/main/java/com/example/ui/screens/MathCaptchaScreen.kt', 'w') as f:
    f.write(content)

