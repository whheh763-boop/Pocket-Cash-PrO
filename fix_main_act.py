import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Fix the trailing braces error
bad_tail = """                composable("daily_rewards") {
                    DailyRewardsScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
                    )
                }
            }
        }
    }
}"""

good_tail = """                composable("daily_rewards") {
                    DailyRewardsScreen(
                        viewModel = mainViewModel,
                        onBack = { rootNavController.popBackStack() }
                    )
                }
            }
        }
    }
}"""

content = content.replace(bad_tail, good_tail)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
