import re

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'r') as f:
    content = f.read()

# Replace the generic error message to append the actual exception message for debugging
old_error_logic = """                                    val errorMsg = when {
                                        msg.contains("credential is incorrect", ignoreCase = true) -> "Invalid email or password."
                                        msg.contains("invalid", ignoreCase = true) -> "Invalid email or password."
                                        msg.contains("already in use", ignoreCase = true) -> "This email is already registered."
                                        msg.contains("badly formatted", ignoreCase = true) -> "Invalid email format."
                                        else -> "Authentication failed. Please try again."
                                    }"""

new_error_logic = """                                    val errorMsg = when {
                                        msg.contains("credential is incorrect", ignoreCase = true) -> "Invalid email or password."
                                        msg.contains("invalid", ignoreCase = true) -> "Invalid email or password."
                                        msg.contains("already in use", ignoreCase = true) -> "This email is already registered."
                                        msg.contains("badly formatted", ignoreCase = true) -> "Invalid email format."
                                        else -> "Auth failed: $msg"
                                    }"""

content = content.replace(old_error_logic, new_error_logic)

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'w') as f:
    f.write(content)
