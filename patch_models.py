with open('app/src/main/java/com/example/model/Models.kt', 'r') as f:
    content = f.read()

content = content.replace('val displayName: String = "Guest User",', 'val displayName: String = "",')

with open('app/src/main/java/com/example/model/Models.kt', 'w') as f:
    f.write(content)
