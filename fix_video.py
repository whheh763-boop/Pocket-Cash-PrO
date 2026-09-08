with open('app/src/main/java/com/example/ui/screens/WatchVideoScreen.kt', 'r') as f:
    lines = f.readlines()

# find button
start = -1
for i, l in enumerate(lines):
    if "Button(" in l and "onClick =" in lines[i+1]:
        start = i
        break

lines[77] = "                onClick = {\n"
lines[78] = "                    if (activity != null) {\n"
lines[79] = "                        isWatching = true\n"
lines[80] = "                        val taskId = java.util.UUID.randomUUID().toString()\n"
lines[81] = "                        viewModel.startSecureTask(taskId)\n"
lines[82] = "                        AdsManager.showRewardedAd(\n"
lines[83] = "                            activity = activity,\n"
lines[84] = "                            onRewardEarned = {\n"
lines[85] = "                                viewModel.claimSecureReward(taskId, 15, \"Video Reward\", 8000L, onSuccess = {\n"
lines[86] = "                                    android.widget.Toast.makeText(context, \"You earned 15 coins!\", android.widget.Toast.LENGTH_SHORT).show()\n"
lines[87] = "                                }, onError = { err ->\n"
lines[88] = "                                    android.widget.Toast.makeText(context, err, android.widget.Toast.LENGTH_SHORT).show()\n"
lines[89] = "                                })\n"
lines[90] = "                            },\n"
lines[91] = "                            onAdDismissed = {\n"
lines[92] = "                                isWatching = false\n"
lines[93] = "                            }\n"
lines[94] = "                        )\n"
lines[95] = "                    }\n"
lines[96] = "                },\n"

with open('app/src/main/java/com/example/ui/screens/WatchVideoScreen.kt', 'w') as f:
    f.writelines(lines)
