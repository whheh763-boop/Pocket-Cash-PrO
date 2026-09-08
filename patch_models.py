with open('app/src/main/java/com/example/model/Models.kt', 'r') as f:
    content = f.read()

content = content.replace("val referralBonus: Int = 100,", "val signupBonus: Int = 50,\n    val referralBonus: Int = 100,")

with open('app/src/main/java/com/example/model/Models.kt', 'w') as f:
    f.write(content)
