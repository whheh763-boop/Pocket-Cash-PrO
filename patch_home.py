with open('app/src/main/java/com/example/ui/screens/HomeScreen.kt', 'a') as f:
    f.write("""
@Composable
fun IconButtonGlass(icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(Color(0xFF0F172A).copy(alpha = 0.65f), RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}
""")
