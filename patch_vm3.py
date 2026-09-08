with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace("repository.performCheckIn(currentUid)", "repository.performCheckIn(currentUid, _appConfig.value.dailyCheckInReward)")

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
