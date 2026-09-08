import re

with open('app/src/main/java/com/example/ui/screens/WatchVideoScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("viewModel.claimSecureReward(taskId, 15,", "viewModel.claimSecureReward(taskId, viewModel.appConfig.value.videoReward,")
content = content.replace("\"You earned 15 coins!\"", "\"You earned ${viewModel.appConfig.value.videoReward} coins!\"")
content = content.replace("Watch Ad (+15 Coins)", "Watch Ad (+${viewModel.appConfig.value.videoReward} Coins)")

with open('app/src/main/java/com/example/ui/screens/WatchVideoScreen.kt', 'w') as f:
    f.write(content)
