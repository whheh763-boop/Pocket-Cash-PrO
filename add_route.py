import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('import com.example.ui.screens.DailyRewardsScreen', 'import com.example.ui.screens.DailyRewardsScreen\nimport com.example.ui.screens.SpeedMathBlitzScreen')

route = """                composable("daily_rewards") {
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
            }"""

content = re.sub(r'                composable\("daily_rewards"\) \{\s*DailyRewardsScreen\(\s*viewModel = mainViewModel,\s*onBack = \{ rootNavController.popBackStack\(\) \}\s*\)\s*\}\s*\}', route, content)

with open('app/src/main/java/com/example/MainActivity.kt', 'w', encoding='utf-8') as f:
    f.write(content)
