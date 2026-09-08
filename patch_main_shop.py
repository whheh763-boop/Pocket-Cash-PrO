import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace("ShoppingDealsScreen(onBack = { rootNavController.popBackStack() })", "ShoppingDealsScreen(viewModel = mainViewModel, onBack = { rootNavController.popBackStack() })")

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
