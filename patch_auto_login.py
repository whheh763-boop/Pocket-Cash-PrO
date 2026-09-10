import re

with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'r') as f:
    content = f.read()

imports = """import androidx.compose.ui.platform.LocalContext
import com.example.viewmodel.MainViewModel"""

if "LocalContext" not in content:
    content = content.replace("import kotlinx.coroutines.delay", "import kotlinx.coroutines.delay\n" + imports)

# Update SplashScreen signature
old_sig = "fun SplashScreen(onNavigateNext: () -> Unit) {"
new_sig = "fun SplashScreen(viewModel: MainViewModel, onNavigateToAuth: () -> Unit, onNavigateToHome: () -> Unit) {"
content = content.replace(old_sig, new_sig)

# Update logic
old_logic = """    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // 2.5 seconds splash
        onNavigateNext()
    }"""

new_logic = """    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // 2.5 seconds splash
        
        // Auto-login check
        val isLogged = viewModel.isLoggedIn()
        if (isLogged) {
            onNavigateToHome()
        } else {
            onNavigateToAuth()
        }
    }"""
content = content.replace(old_logic, new_logic)

with open('app/src/main/java/com/example/ui/screens/SplashScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    main = f.read()

main = main.replace("""                    SplashScreen(
                        onNavigateNext = { rootNavController.navigate("auth") { popUpTo("splash") { inclusive = true } } }
                    )""", """                    SplashScreen(
                        viewModel = mainViewModel,
                        onNavigateToAuth = { rootNavController.navigate("auth") { popUpTo("splash") { inclusive = true } } },
                        onNavigateToHome = { rootNavController.navigate("main") { popUpTo("splash") { inclusive = true } } }
                    )""")

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(main)
    
