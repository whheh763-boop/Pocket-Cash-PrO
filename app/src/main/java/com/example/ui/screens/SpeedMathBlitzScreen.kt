package com.example.ui.screens

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
