import re

with open('app/src/main/java/com/example/ui/screens/MathCaptchaScreen.kt', 'r') as f:
    content = f.read()

math_replacement = """                        onSuccess = {
                            activity?.let {
                                val taskId = java.util.UUID.randomUUID().toString()
                                viewModel.startSecureTask(taskId)
                                AdsManager.showRewardedAd(
                                    activity = it,
                                    onRewardEarned = {
                                        viewModel.claimSecureReward(taskId, 10, "Math Quiz Reward", 3000L, onSuccess = {
                                            feedbackMessage = "Correct! +10 Coins"
                                        }, onError = { err ->
                                            feedbackMessage = err
                                        })
                                    },
                                    onAdDismissed = {
                                        if (feedbackMessage.isEmpty() || !feedbackMessage.contains("10 Coins")) {
                                            feedbackMessage = "No Ad available. Attempt lost."
                                        }
                                        answerInput = ""
                                        num1 = Random.nextInt(1, 50)
                                        num2 = Random.nextInt(1, 50)
                                        isLoading = false
                                    }
                                )
                            } ?: run {"""

captcha_replacement = """                        onSuccess = {
                            activity?.let {
                                val taskId = java.util.UUID.randomUUID().toString()
                                viewModel.startSecureTask(taskId)
                                AdsManager.showRewardedAd(
                                    activity = it,
                                    onRewardEarned = {
                                        viewModel.claimSecureReward(taskId, 5, "Captcha Reward", 3000L, onSuccess = {
                                            feedbackMessage = "Correct! +5 Coins"
                                        }, onError = { err ->
                                            feedbackMessage = err
                                        })
                                    },
                                    onAdDismissed = {
                                        if (feedbackMessage.isEmpty() || !feedbackMessage.contains("5 Coins")) {
                                            feedbackMessage = "No Ad available. Attempt lost."
                                        }
                                        answerInput = ""
                                        currentCaptcha = generateCaptcha()
                                        isLoading = false
                                    }
                                )
                            } ?: run {"""

content = re.sub(r'                        onSuccess = \{[\s\S]*?activity\?\.let \{[\s\S]*?AdsManager\.showRewardedAd\([\s\S]*?activity = it,[\s\S]*?onRewardEarned = \{[\s\S]*?viewModel\.addCoins\(10, "Math Quiz Reward"\)[\s\S]*?feedbackMessage = "Correct! \+10 Coins"[\s\S]*?\},[\s\S]*?onAdDismissed = \{[\s\S]*?if \(feedbackMessage\.isEmpty\(\) \|\| !feedbackMessage\.contains\("10 Coins"\)\) \{[\s\S]*?feedbackMessage = "No Ad available\. Attempt lost\."[\s\S]*?\}[\s\S]*?answerInput = ""[\s\S]*?num1 = Random\.nextInt\(1, 50\)[\s\S]*?num2 = Random\.nextInt\(1, 50\)[\s\S]*?isLoading = false[\s\S]*?\}[\s\S]*?\)[\s\S]*?\} \?: run \{', math_replacement, content)

content = re.sub(r'                        onSuccess = \{[\s\S]*?activity\?\.let \{[\s\S]*?AdsManager\.showRewardedAd\([\s\S]*?activity = it,[\s\S]*?onRewardEarned = \{[\s\S]*?viewModel\.addCoins\(5, "Captcha Reward"\)[\s\S]*?feedbackMessage = "Correct! \+5 Coins"[\s\S]*?\},[\s\S]*?onAdDismissed = \{[\s\S]*?if \(feedbackMessage\.isEmpty\(\) \|\| !feedbackMessage\.contains\("5 Coins"\)\) \{[\s\S]*?feedbackMessage = "No Ad available\. Attempt lost\."[\s\S]*?\}[\s\S]*?answerInput = ""[\s\S]*?currentCaptcha = generateCaptcha\(\)[\s\S]*?isLoading = false[\s\S]*?\}[\s\S]*?\)[\s\S]*?\} \?: run \{', captcha_replacement, content)


with open('app/src/main/java/com/example/ui/screens/MathCaptchaScreen.kt', 'w') as f:
    f.write(content)
