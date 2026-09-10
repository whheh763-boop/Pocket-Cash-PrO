import re

with open('app/src/main/java/com/example/ui/screens/MathCaptchaScreen.kt', 'r') as f:
    content = f.read()

# Change the display from "$captchasLeft/20" to "${20 - captchasLeft}/20" so it shows Completed/Total
# But actually, the screenshot says "20/20" for DAILY CAPTCHAS, which makes it look like it's full/completed.
# So if it starts at 20, let's display "Remaining: $captchasLeft"
# Or let's just show "$captchasLeft Left"
content = content.replace('Text("$captchasLeft/20", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)', 'Text("$captchasLeft Left", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)')

with open('app/src/main/java/com/example/ui/screens/MathCaptchaScreen.kt', 'w') as f:
    f.write(content)
