package com.youmenotes.flagfindergame.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.youmenotes.flagfindergame.ui.viewmodel.QuizScreenViewModel

@Composable
fun ResultScreen(
    quizViewModel: QuizScreenViewModel,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val score = quizViewModel.score.collectAsState(initial = 0).value
    val totalQuestions = quizViewModel.totalQuestions.collectAsState(initial = 0).value

    // Display the result screen layout
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quiz Completed!",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Score: $score / $totalQuestions",
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onRestart) {
            Text(text = "Restart Quiz")
        }
    }
}