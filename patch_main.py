import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r', encoding='utf-8') as f:
    content = f.read()

route = """                composable("math_blitz") {
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
                }"""

content = content.replace("""                composable("math_blitz") {
                    SpeedMathBlitzScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }""", route)

with open('app/src/main/java/com/example/MainActivity.kt', 'w', encoding='utf-8') as f:
    f.write(content)
