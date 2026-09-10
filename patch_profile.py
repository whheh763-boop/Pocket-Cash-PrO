import re

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'r') as f:
    content = f.read()

# Replace Image
old_image = '"https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80"'
new_image = '"https://t4.ftcdn.net/jpg/09/44/23/91/360_F_944239186_kbKzpH8DsEq6eSGXqKvPHeH8fOwvrx8r.jpg"'
content = content.replace(old_image, new_image)

# Replace Social Links Block
old_social = """            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "WhatsApp", color = IconTintWhatsapp, icon = Icons.AutoMirrored.Filled.Chat) {
                    if (appConfig.whatsappLink.isNotEmpty()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appConfig.whatsappLink)))
                }
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "Telegram", color = IconTintTelegram, icon = Icons.AutoMirrored.Filled.Send) {
                    if (appConfig.telegramLink.isNotEmpty()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appConfig.telegramLink)))
                }
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "Instagram", color = IconTintInstagram, icon = Icons.Default.CameraAlt) {
                    if (appConfig.instagramLink.isNotEmpty()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appConfig.instagramLink)))
                }
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "YouTube", color = IconTintYoutube, icon = Icons.Default.PlayArrow) {
                    if (appConfig.youtubeLink.isNotEmpty()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appConfig.youtubeLink)))
                }
            }"""

new_social = """            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "WhatsApp", color = IconTintWhatsapp, icon = Icons.AutoMirrored.Filled.Chat) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/9779706612914")))
                }
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "TikTok", color = Color(0xFF000000), icon = Icons.Default.MusicVideo) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@sanjay1.50?_r=1&_t=ZS-99arKzforYa")))
                }
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "Instagram", color = IconTintInstagram, icon = Icons.Default.CameraAlt) {
                    if (appConfig.instagramLink.isNotEmpty()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appConfig.instagramLink)))
                }
                SocialBtnPremium(modifier = Modifier.weight(1f), name = "YouTube", color = IconTintYoutube, icon = Icons.Default.PlayArrow) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/@sanjay_hack_2.99?si=_-CljQLWfVSHBe4x1")))
                }
            }"""

content = content.replace(old_social, new_social)

# We need to make sure MusicVideo icon is imported
if "import androidx.compose.material.icons.filled.MusicVideo" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.PlayArrow", "import androidx.compose.material.icons.filled.PlayArrow\nimport androidx.compose.material.icons.filled.MusicVideo")


with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)
