package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.FeedbackScreen

import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.MainAppScreen
import com.example.ui.screens.MathCaptchaScreen
import com.example.ui.screens.ShoppingDealsScreen
import com.example.ui.screens.TransactionHistoryScreen
import com.example.ui.screens.SpinWheelScreen

import com.example.ui.screens.AdminFeedbacksScreen

import com.example.ui.screens.AdminPanelScreen

import com.example.ui.screens.ScratchCardScreen
import com.example.ui.screens.WatchVideoScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.MathEarnScreen
import com.example.ui.screens.CardMatchScreen
import com.example.ui.screens.DailyRewardsScreen
import com.example.ui.screens.SpeedMathBlitzScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.ads.AdsManager
import com.example.utils.SecurityManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    
    if (SecurityManager.isVpnActive(this) || !SecurityManager.isDeviceSecure()) {
        Toast.makeText(this, "Anti-Cheat: VPN or Unauthorized Device Detected. Access Denied.", Toast.LENGTH_LONG).show()
        finish()
        return
    }

    enableEdgeToEdge()
    AdsManager.initialize(this)
    setContent {
        PocketCashApp()
    }
  }

  override fun onResume() {
      super.onResume()
      AdsManager.showAdIfAvailable(this)
  }
}

@Composable
fun PocketCashApp() {
    val rootNavController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()
    
    MyApplicationTheme(darkTheme = isDarkMode) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            NavHost(navController = rootNavController, startDestination = "splash") {
                composable("splash") {
                    SplashScreen(
                        viewModel = mainViewModel,
                        onNavigateToAuth = { rootNavController.navigate("auth") { popUpTo("splash") { inclusive = true } } },
                        onNavigateToHome = { rootNavController.navigate("main") { popUpTo("splash") { inclusive = true } } }
                    )
                }
                composable("auth") {
                    AuthScreen(
                        viewModel = mainViewModel,
                        onNavigateToHome = { rootNavController.navigate("main") { popUpTo("auth") { inclusive = true } } },
                        onNavigateToForgotPassword = { rootNavController.navigate("forgot_password") }
                    )
                }
                composable("forgot_password") {
                    ForgotPasswordScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                composable("main") {
                    MainAppScreen(mainViewModel, rootNavController)
                }
                composable("watch_video") {
                    WatchVideoScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                composable("tasks") {
                    MathCaptchaScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                composable("deals") {
                    ShoppingDealsScreen(viewModel = mainViewModel, onBack = { rootNavController.popBackStack() })
                }
                composable("history") {
                    TransactionHistoryScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                
                composable("admin_feedbacks") {
                    AdminFeedbacksScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }

                composable("admin_panel") {
                    AdminPanelScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() },
                        onNavigateToFeedbacks = { rootNavController.navigate("admin_feedbacks") }
                    )
                }

                composable("spin") {
                    SpinWheelScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                composable("scratch") {
                    ScratchCardScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                composable("quiz") {
                    QuizScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                
                composable("privacy_policy") {
                    PrivacyPolicyScreen(
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                composable("feedback") {
                    FeedbackScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }

                composable("daily_rewards") {
                    DailyRewardsScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                
                composable("math_blitz") {
                    SpeedMathBlitzScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                
                composable("card_match") {
                    CardMatchScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
            }
        }
    }
}
