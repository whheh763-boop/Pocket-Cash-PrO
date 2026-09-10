import re

quiz_content = """package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.viewmodel.MainViewModel

@Composable
fun QuizScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    SecureQuizTemplate(
        viewModel = viewModel,
        quizType = "GK",
        title = "GK Quiz Earn",
        onBack = onBack
    )
}
"""

math_content = """package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.viewmodel.MainViewModel

@Composable
fun SpeedMathBlitzScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    SecureQuizTemplate(
        viewModel = viewModel,
        quizType = "MATH",
        title = "Math Earn",
        onBack = onBack
    )
}
"""

with open('app/src/main/java/com/example/ui/screens/QuizScreen.kt', 'w') as f:
    f.write(quiz_content)

with open('app/src/main/java/com/example/ui/screens/SpeedMathBlitzScreen.kt', 'w') as f:
    f.write(math_content)
