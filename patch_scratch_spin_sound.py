import re

def add_sound(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    sound_logic = """                        onSuccess = {
                                                            showRewardDialog = true
                                                            try {
                                                                val notification = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                                                                val r = android.media.RingtoneManager.getRingtone(context, notification)
                                                                r.play()
                                                            } catch (e: Exception) { e.printStackTrace() }
                                                        },"""
                                                        
    if "ScratchCardScreen.kt" in filepath:
        content = re.sub(r'                        onSuccess = \{\s*showRewardDialog = true\s*\},', sound_logic.strip(), content)
    else:
        sound_logic = sound_logic.replace("                                                        ", "                                            ")
        content = re.sub(r'                                            onSuccess = \{\s*showRewardDialog = true\s*\},', sound_logic.strip(), content)
        
    with open(filepath, 'w') as f:
        f.write(content)

add_sound('app/src/main/java/com/example/ui/screens/ScratchCardScreen.kt')
add_sound('app/src/main/java/com/example/ui/screens/SpinWheelScreen.kt')
