import re

with open('app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

imports = """import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import android.widget.Toast
import com.example.ui.BiometricHelper"""
content = content.replace("import com.example.viewmodel.MainViewModel", "import com.example.viewmodel.MainViewModel\n" + imports)

# We define the biometric function inside the Composable
auth_func = """    val currentRoute = navBackStackEntry?.destination?.route
    
    val context = LocalContext.current
    val handleSecureNavigation = { route: String, isRoot: Boolean ->
        val activity = context as? FragmentActivity
        if (activity != null) {
            BiometricHelper.authenticate(
                activity = activity,
                onSuccess = {
                    if (isRoot) {
                        rootNavController.navigate(route)
                    } else {
                        bottomNavController.navigate(route) {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onError = { err ->
                    Toast.makeText(context, "Authentication failed: $err", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, "Activity is not a FragmentActivity", Toast.LENGTH_SHORT).show()
        }
    }"""

content = content.replace("    val currentRoute = navBackStackEntry?.destination?.route", auth_func)

# Replace navigation paths
# 1. NavigationBarItem for withdraw_tab
content = content.replace('onClick = { bottomNavController.navigate("withdraw_tab") { popUpTo(bottomNavController.graph.startDestinationId) { saveState = true } ; launchSingleTop = true; restoreState = true } }', 'onClick = { handleSecureNavigation("withdraw_tab", false) }')

# 2. HomeScreen -> onNavigateToWallet
content = content.replace('onNavigateToWallet = { bottomNavController.navigate("withdraw_tab") }', 'onNavigateToWallet = { handleSecureNavigation("withdraw_tab", false) }')

# 3. ProfileScreen -> onNavigateToHistory
content = content.replace('onNavigateToHistory = { rootNavController.navigate("history") },', 'onNavigateToHistory = { handleSecureNavigation("history", true) },')

# 4. WalletScreen -> onNavigateToHistory
content = content.replace('onNavigateToHistory = { rootNavController.navigate("history") }\n                )', 'onNavigateToHistory = { handleSecureNavigation("history", true) }\n                )')


with open('app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
