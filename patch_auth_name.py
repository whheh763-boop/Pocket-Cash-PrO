import re

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'r') as f:
    content = f.read()

# Add variable for name
content = content.replace("var email by remember { mutableStateOf(\"\") }", "var email by remember { mutableStateOf(\"\") }\n    var name by remember { mutableStateOf(\"\") }")

# Add text field for name
name_field = """                    AnimatedVisibility(visible = !isLoginMode) {
                        Column {
                            AuthTextField(
                                value = name,
                                onValueChange = { name = it },
                                placeholder = "Full Name",
                                icon = Icons.Default.Person,
                                keyboardType = KeyboardType.Text
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    
                    AuthTextField(
                        value = email,
"""
content = content.replace("                    AuthTextField(\n                        value = email,", name_field)

# Add requirement for name
content = content.replace('if (email.isBlank() || password.isBlank()) {\n                                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()', 'if (email.isBlank() || password.isBlank() || (!isLoginMode && name.isBlank())) {\n                                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()')

# Add name parameter to signup
content = content.replace("viewModel.signup(email, password, selectedCountry, referralCode, deviceId)", "viewModel.signup(email, password, name, selectedCountry, referralCode, deviceId)")

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'w') as f:
    f.write(content)
