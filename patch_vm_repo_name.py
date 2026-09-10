import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace("suspend fun signup(email: String, pass: String, country: Country, refCode: String, deviceId: String)", "suspend fun signup(email: String, pass: String, name: String, country: Country, refCode: String, deviceId: String)")
content = content.replace("val uid = repository.signUpWithEmail(email, pass, country, refCode, deviceId)", "val uid = repository.signUpWithEmail(email, pass, name, country, refCode, deviceId)")

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

content = content.replace("suspend fun signUpWithEmail(email: String, pass: String, country: Country, refCode: String, deviceId: String)", "suspend fun signUpWithEmail(email: String, pass: String, name: String, country: Country, refCode: String, deviceId: String)")

new_user = """                val newUser = User(
                    deviceId = deviceId,
                    lastResetDate = currentDate,
                    uid = uid,
                    email = email,
                    displayName = name,
                    country = country,
                    referralCode = myReferralCode,
                    referredBy = referredByUid
                )"""

old_user = """                val newUser = User(
                    deviceId = deviceId,
                    lastResetDate = currentDate,
                    uid = uid,
                    email = email,
                    country = country,
                    referralCode = myReferralCode,
                    referredBy = referredByUid
                )"""

content = content.replace(old_user, new_user)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
