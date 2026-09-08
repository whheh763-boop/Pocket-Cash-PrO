import re

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'r') as f:
    content = f.read()

# I will add the Quiz section right above "Save Configuration" button
quiz_section = """
                Text("Quiz Game Controls", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                
                var newQuizQuestion by remember { mutableStateOf("") }
                var newQuizOption1 by remember { mutableStateOf("") }
                var newQuizOption2 by remember { mutableStateOf("") }
                var newQuizOption3 by remember { mutableStateOf("") }
                var newQuizOption4 by remember { mutableStateOf("") }
                var newQuizCorrectIndex by remember { mutableStateOf("0") }
                var newQuizReward by remember { mutableStateOf("10") }
                var isAddingQuiz by remember { mutableStateOf(false) }

                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Add New Question", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        OutlinedTextField(value = newQuizQuestion, onValueChange = { newQuizQuestion = it }, label = { Text("Question") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizOption1, onValueChange = { newQuizOption1 = it }, label = { Text("Option 1") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizOption2, onValueChange = { newQuizOption2 = it }, label = { Text("Option 2") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizOption3, onValueChange = { newQuizOption3 = it }, label = { Text("Option 3") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizOption4, onValueChange = { newQuizOption4 = it }, label = { Text("Option 4") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizCorrectIndex, onValueChange = { newQuizCorrectIndex = it }, label = { Text("Correct Index (0-3)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newQuizReward, onValueChange = { newQuizReward = it }, label = { Text("Reward Coins") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                        
                        Button(
                            onClick = {
                                if (newQuizQuestion.isNotEmpty() && newQuizOption1.isNotEmpty()) {
                                    isAddingQuiz = true
                                    val q = com.example.model.QuizQuestion(
                                        question = newQuizQuestion,
                                        options = listOf(newQuizOption1, newQuizOption2, newQuizOption3, newQuizOption4).filter { it.isNotEmpty() },
                                        correctAnswerIndex = newQuizCorrectIndex.toIntOrNull() ?: 0,
                                        reward = newQuizReward.toIntOrNull() ?: 10
                                    )
                                    viewModel.addQuizQuestion(q, onSuccess = {
                                        isAddingQuiz = false
                                        newQuizQuestion = ""
                                        newQuizOption1 = ""
                                        newQuizOption2 = ""
                                        newQuizOption3 = ""
                                        newQuizOption4 = ""
                                        android.widget.Toast.makeText(context, "Added Quiz", android.widget.Toast.LENGTH_SHORT).show()
                                    }, onError = {
                                        isAddingQuiz = false
                                        android.widget.Toast.makeText(context, "Failed to add", android.widget.Toast.LENGTH_SHORT).show()
                                    })
                                }
                            },
                            enabled = !isAddingQuiz,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("Save Question")
                        }
                    }
                }
"""

content = content.replace("Button(", quiz_section + "\n                Button(", 1)

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'w') as f:
    f.write(content)
