with open('gradle/libs.versions.toml', 'r') as f:
    content = f.read()

content = content.replace('googleid = "1.1.1"', 'googleid = "1.1.1"\nbiometric = "1.1.0"')
content = content.replace('googleid = { group = "com.google.android.libraries.identity.googleid", name = "googleid", version.ref = "googleid" }', 'googleid = { group = "com.google.android.libraries.identity.googleid", name = "googleid", version.ref = "googleid" }\nandroidx-biometric = { group = "androidx.biometric", name = "biometric", version.ref = "biometric" }')

with open('gradle/libs.versions.toml', 'w') as f:
    f.write(content)
