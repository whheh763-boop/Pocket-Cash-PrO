import re

with open('app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

# Update HomeScreen call
content = content.replace(
    "onNavigateToLeaderboard = { bottomNavController.navigate(\"leaderboard_tab\") }\n                )",
    "onNavigateToLeaderboard = { bottomNavController.navigate(\"leaderboard_tab\") },\n                    onNavigateToMathEarn = { rootNavController.navigate(\"math_earn\") }\n                )"
)

with open('app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)


with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Add Math Earn route
quiz_route = """                composable("quiz") {
                    QuizScreen(
                        viewModel = mainViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }"""
math_earn_route = """                composable("quiz") {
                    QuizScreen(
                        viewModel = mainViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("math_earn") {
                    MathEarnScreen(
                        viewModel = mainViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }"""

content = content.replace(quiz_route, math_earn_route)

# Add import
import_quiz = "import com.example.ui.screens.QuizScreen"
if import_quiz in content:
    content = content.replace(import_quiz, import_quiz + "\nimport com.example.ui.screens.MathEarnScreen")

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

