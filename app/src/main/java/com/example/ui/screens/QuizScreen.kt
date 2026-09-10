package com.example.ui.screens

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
