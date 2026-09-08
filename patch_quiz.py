import re

with open('app/src/main/java/com/example/ui/screens/QuizScreen.kt', 'r') as f:
    content = f.read()

# Add dialog import and state
import_window = "import androidx.compose.ui.window.Dialog"
if import_window not in content:
    content = content.replace("import androidx.compose.ui.unit.sp", "import androidx.compose.ui.unit.sp\n" + import_window)

# Add state
state_code = """    var showResults by remember { mutableStateOf(false) }
    var showWrongAnswerDialog by remember { mutableStateOf(false) }"""
content = content.replace("    var showResults by remember { mutableStateOf(false) }", state_code)

# Update onClick
onClick_old = """                            onClick = {
                                if (!isAnswerChecked) {
                                    isAnswerChecked = true
                                    if (selectedOption == question.answer) {
                                        score++
                                        coinsEarned += 10
                                        viewModel.addCoins(10, "Quiz Answer Correct")
                                        Toast.makeText(context, "+10 Coins!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {"""
onClick_new = """                            onClick = {
                                if (!isAnswerChecked) {
                                    isAnswerChecked = true
                                    if (selectedOption == question.answer) {
                                        score++
                                        coinsEarned += 10
                                        viewModel.addCoins(10, "Quiz Answer Correct")
                                        Toast.makeText(context, "+10 Coins!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        showWrongAnswerDialog = true
                                    }
                                } else {"""
content = content.replace(onClick_old, onClick_new)

# Add the Dialog code at the end of the file
dialog_code = """
@Composable
fun WrongAnswerReviveDialog(onWatchAd: () -> Unit, onSkip: () -> Unit) {
    Dialog(onDismissRequest = onSkip) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(QuizGlassBg)
                .border(1.dp, QuizRed.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(QuizRed.copy(alpha = 0.15f))
                        .border(1.dp, QuizRed.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("😢", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("गलत उत्तर!", color = QuizRed, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "You selected the wrong answer. Want to watch a short ad to try again?",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onWatchAd,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuizPrimary)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Watch Ad to Retry", color = Color.White, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                TextButton(onClick = onSkip) {
                    Text("Skip & Next", color = QuizTextMuted)
                }
            }
        }
    }
}
"""
content += dialog_code

# Inject the dialog call before the last closing brace of the main function Box
dialog_call = """
        if (showWrongAnswerDialog) {
            WrongAnswerReviveDialog(
                onWatchAd = {
                    activity?.let {
                        AdsManager.showRewardedAd(
                            activity = it,
                            onRewardEarned = {
                                isAnswerChecked = false
                                selectedOption = null
                                showWrongAnswerDialog = false
                                Toast.makeText(context, "Second chance granted!", Toast.LENGTH_SHORT).show()
                            },
                            onAdDismissed = {
                                showWrongAnswerDialog = false
                            }
                        )
                    }
                },
                onSkip = {
                    showWrongAnswerDialog = false
                }
            )
        }
"""
content = content.replace("    }\n}\n\n@Composable", dialog_call + "    }\n}\n\n@Composable")

with open('app/src/main/java/com/example/ui/screens/QuizScreen.kt', 'w') as f:
    f.write(content)

