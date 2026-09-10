import re

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'r') as f:
    content = f.read()

# I will add FeaturedGameCard function
featured_card = """
@Composable
fun FeaturedGameCard(
    title: String,
    subtitle: String,
    imageUrl: String,
    bgGradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(bgGradient))
            .clickable { onClick() }
    ) {
        // Shine effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(300f, 300f)
                ))
        )
        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = 10.dp, y = (-5).dp)
            )
        }
    }
}
"""

if "FeaturedGameCard" not in content:
    content = content.replace("@Composable\nfun ActionCard(", featured_card + "\n@Composable\nfun ActionCard(")

# Now I'll replace the old Spin Wheel and Scratch cards in the layout.
# We have a grid layout of Row -> ActionCard, ActionCard.
# I want to put Spin Wheel and Scratch as full-width FeaturedGameCards, maybe stacked before or after the 2x2 grid.

old_grid_code = """                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            ActionCard(
                                title = "Daily Check-in",
                                icon = Icons.Default.CardGiftcard,
                                iconColor = HomeNeonGold,
                                bgColor = HomeNeonGold.copy(alpha = 0.12f),
                                onClick = onNavigateToDailyRewards,
                                modifier = Modifier.weight(1f)
                            )
                            ActionCard(
                                title = "Spin Wheel",
                                icon = Icons.Default.Refresh,
                                iconColor = HomeNeonCyan,
                                bgColor = HomeNeonCyan.copy(alpha = 0.12f),
                                onClick = onNavigateToSpin,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            ActionCard(
                                title = "Scratch",
                                icon = Icons.Default.ConfirmationNumber,
                                iconColor = HomeNeonGreen,
                                bgColor = HomeNeonGreen.copy(alpha = 0.12f),
                                onClick = onNavigateToScratch,
                                modifier = Modifier.weight(1f)
                            )
                            ActionCard(
                                title = "Quiz Blitz",
                                icon = Icons.Default.Psychology,
                                iconColor = HomeNeonPink,
                                bgColor = HomeNeonPink.copy(alpha = 0.12f),
                                onClick = onNavigateToQuiz,
                                modifier = Modifier.weight(1f)
                            )
                        }"""

new_grid_code = """                        // Top 2 Big Featured Games
                        FeaturedGameCard(
                            title = "Spin & Win",
                            subtitle = "Try your luck daily!",
                            imageUrl = "https://cdn-icons-png.flaticon.com/512/3063/3063822.png", // Beautiful 3D spin wheel
                            bgGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)), // Indigo to Purple
                            onClick = onNavigateToSpin,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )
                        
                        FeaturedGameCard(
                            title = "Scratch Card",
                            subtitle = "Scratch to earn coins",
                            imageUrl = "https://cdn-icons-png.flaticon.com/512/7515/7515093.png", // 3D Scratch card / Ticket
                            bgGradient = listOf(Color(0xFFF59E0B), Color(0xFFF97316)), // Amber to Orange
                            onClick = onNavigateToScratch,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        // 2x1 Grid for the rest
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            ActionCard(
                                title = "Daily Bonus",
                                icon = Icons.Default.CardGiftcard,
                                iconColor = HomeNeonGold,
                                bgColor = HomeNeonGold.copy(alpha = 0.12f),
                                onClick = onNavigateToDailyRewards,
                                modifier = Modifier.weight(1f)
                            )
                            ActionCard(
                                title = "Quiz Blitz",
                                icon = Icons.Default.Psychology,
                                iconColor = HomeNeonPink,
                                bgColor = HomeNeonPink.copy(alpha = 0.12f),
                                onClick = onNavigateToQuiz,
                                modifier = Modifier.weight(1f)
                            )
                        }"""

content = content.replace(old_grid_code, new_grid_code)

if "import androidx.compose.ui.layout.ContentScale" not in content:
    content = content.replace("import coil.compose.AsyncImage", "import coil.compose.AsyncImage\nimport androidx.compose.ui.layout.ContentScale")

with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'w') as f:
    f.write(content)
