import re

with open('app/src/main/java/com/example/ui/screens/DailyRewardsScreen.kt', 'r') as f:
    content = f.read()

sound_logic = """                        onClick = {
                            viewModel.performDailyCheckIn()
                            // Play sound
                            try {
                                val notification = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                                val r = android.media.RingtoneManager.getRingtone(context, notification)
                                r.play()
                                Toast.makeText(context, "Claimed successfully!", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },"""

content = re.sub(r'                        onClick = \{[\s\S]*?\},', sound_logic, content)

with open('app/src/main/java/com/example/ui/screens/DailyRewardsScreen.kt', 'w') as f:
    f.write(content)
