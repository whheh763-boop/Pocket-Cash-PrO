import re

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'r') as f:
    content = f.read()

# Make sure AuthScreen correctly checks userState if the user bypasses SplashScreen or logic
import_str = "import androidx.compose.runtime.LaunchedEffect\n"
if "LaunchedEffect" not in content:
    content = content.replace("import androidx.compose.runtime.*", import_str + "import androidx.compose.runtime.*")

logic = """    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    
    val userState by viewModel.userState.collectAsState()
    
    LaunchedEffect(userState) {
        if (viewModel.isLoggedIn() && userState != null) {
            onNavigateToHome()
        }
    }
    
    val prefs = context.getSharedPreferences("PocketCashAuth", android.content.Context.MODE_PRIVATE)"""
    
content = content.replace("""    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    
    val prefs = context.getSharedPreferences("PocketCashAuth", android.content.Context.MODE_PRIVATE)""", logic)

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'w') as f:
    f.write(content)
