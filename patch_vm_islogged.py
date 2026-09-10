import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

new_func = """    fun isLoggedIn(): Boolean {
        return repository.isUserLoggedIn() != null
    }
    
"""

content = content.replace("    fun login(email: String, pass: String) {", new_func + "    fun login(email: String, pass: String) {")

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
