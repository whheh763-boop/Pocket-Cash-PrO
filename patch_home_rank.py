import re

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'r') as f:
    content = f.read()

rank_logic = """                    Column {
                        val userRank = when {
                            (userState?.lifetimeEarnings ?: 0) >= 5000 -> "Pocket Elite 💎"
                            (userState?.lifetimeEarnings ?: 0) >= 1000 -> "Pro Member 🌟"
                            else -> "Level 1 Earner 🚀"
                        }
                        
                        Text(
                            text = if (userState?.displayName.isNullOrBlank() || userState?.displayName == "Guest User") "PocketCash Pro" else userState!!.displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HomeTextMain
                        )
                        Text(
                            text = userRank,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HomeNeonGreen
                        )
                    }"""

old_column = """                    Column {
                        Text(
                            text = userState?.displayName ?: "PocketCash Pro",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HomeTextMain
                        )
                        Text(
                            text = "● ONLINE PRO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HomeNeonGreen
                        )
                    }"""

content = content.replace(old_column, rank_logic)

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'w') as f:
    f.write(content)
