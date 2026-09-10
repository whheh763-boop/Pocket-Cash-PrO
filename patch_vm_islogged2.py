import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

new_func = """    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn() != null
    }
    
    suspend fun login"""

content = content.replace("    suspend fun login", new_func)

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'r') as f:
    splash = f.read()
splash = splash.replace("viewModel.isLoggedIn()", "viewModel.isUserLoggedIn()")
with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'w') as f:
    f.write(splash)
    
with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'r') as f:
    auth = f.read()
auth = auth.replace("viewModel.isLoggedIn()", "viewModel.isUserLoggedIn()")
with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'w') as f:
    f.write(auth)
