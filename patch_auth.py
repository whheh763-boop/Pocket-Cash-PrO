import re

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'r') as f:
    content = f.read()

# Replace the static Checkbox with a working one
# First we need to add SharedPreferences handling
new_init = """    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    
    val prefs = context.getSharedPreferences("PocketCashAuth", android.content.Context.MODE_PRIVATE)
    
    LaunchedEffect(Unit) {
        val savedEmail = prefs.getString("saved_email", "") ?: ""
        val savedPassword = prefs.getString("saved_password", "") ?: ""
        if (savedEmail.isNotEmpty() && savedPassword.isNotEmpty()) {
            email = savedEmail
            password = savedPassword
            rememberMe = true
        }
    }
"""

content = content.replace("    var email by remember { mutableStateOf(\"\") }\n    var password by remember { mutableStateOf(\"\") }", new_init)

checkbox_old = """                                Checkbox(
                                    checked = false, 
                                     onCheckedChange = {}, 
                                     colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF38BDF8), 
                                         uncheckedColor = Color(0xFF64748B)
                                    )
                                )"""

checkbox_new = """                                Checkbox(
                                    checked = rememberMe, 
                                     onCheckedChange = { rememberMe = it }, 
                                     colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF38BDF8), 
                                         uncheckedColor = Color(0xFF64748B)
                                    )
                                )"""

content = content.replace(checkbox_old, checkbox_new)

# Add saving logic on successful login/signup
save_logic = """                                    if (isLoginMode) {
                                        viewModel.login(email, password)
                                    } else {
                                        val deviceId = android.provider.Settings.Secure.getString(context.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
                                        viewModel.signup(email, password, selectedCountry, referralCode, deviceId)
                                    }
                                    
                                    if (rememberMe) {
                                        prefs.edit()
                                            .putString("saved_email", email)
                                            .putString("saved_password", password)
                                            .apply()
                                    } else {
                                        prefs.edit().clear().apply()
                                    }"""

content = content.replace("""                                    if (isLoginMode) {
                                        viewModel.login(email, password)
                                    } else {
                                        val deviceId = android.provider.Settings.Secure.getString(context.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
                                        viewModel.signup(email, password, selectedCountry, referralCode, deviceId)
                                    }""", save_logic)

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'w') as f:
    f.write(content)
