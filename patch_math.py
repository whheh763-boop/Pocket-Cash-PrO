import re

with open('app/src/main/java/com/example/ui/screens/MathCaptchaScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("viewModel.claimSecureReward(taskId, 10,", "viewModel.claimSecureReward(taskId, viewModel.appConfig.value.mathReward,")
content = content.replace("feedbackMessage = \"Correct! +10 Coins\"", "feedbackMessage = \"Correct! +${viewModel.appConfig.value.mathReward} Coins\"")
content = content.replace("!feedbackMessage.contains(\"10 Coins\")", "!feedbackMessage.contains(\"${viewModel.appConfig.value.mathReward} Coins\")")

content = content.replace("viewModel.claimSecureReward(taskId, 5,", "viewModel.claimSecureReward(taskId, viewModel.appConfig.value.captchaReward,")
content = content.replace("feedbackMessage = \"Correct! +5 Coins\"", "feedbackMessage = \"Correct! +${viewModel.appConfig.value.captchaReward} Coins\"")
content = content.replace("!feedbackMessage.contains(\"5 Coins\")", "!feedbackMessage.contains(\"${viewModel.appConfig.value.captchaReward} Coins\")")

with open('app/src/main/java/com/example/ui/screens/MathCaptchaScreen.kt', 'w') as f:
    f.write(content)
