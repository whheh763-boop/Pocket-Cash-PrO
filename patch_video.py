import re

with open('app/src/main/java/com/example/ui/screens/WatchVideoScreen.kt', 'r') as f:
    content = f.read()

replacement = """                    if (activity != null) {
                        isWatching = true
                        val taskId = java.util.UUID.randomUUID().toString()
                        viewModel.startSecureTask(taskId)
                        AdsManager.showRewardedAd(
                            activity = activity,
                            onRewardEarned = {
                                viewModel.claimSecureReward(taskId, 15, "Video Reward", 8000L, onSuccess = {
                                    android.widget.Toast.makeText(context, "You earned 15 coins!", android.widget.Toast.LENGTH_SHORT).show()
                                }, onError = { err ->
                                    android.widget.Toast.makeText(context, err, android.widget.Toast.LENGTH_SHORT).show()
                                })
                            },
                            onAdDismissed = {
                                isWatching = false
                            }
                        )
                    }"""

content = re.sub(r'                    if \(activity != null\) \{[\s\S]*?onAdDismissed = \{[\s\S]*?isWatching = false[\s\S]*?\}[\s\S]*?\}[\s\S]*?\}', replacement, content)

with open('app/src/main/java/com/example/ui/screens/WatchVideoScreen.kt', 'w') as f:
    f.write(content)
