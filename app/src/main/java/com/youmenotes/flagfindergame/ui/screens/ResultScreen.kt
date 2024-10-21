package com.youmenotes.flagfindergame.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.youmenotes.flagfindergame.ui.viewmodel.ResultScreenViewModel

@Composable
fun ResultScreen(
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResultScreenViewModel = viewModel()
) {
    val score by viewModel.score.collectAsState()
    val totalQuestions by viewModel.totalQuestions.collectAsState()

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
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Score: $score / $totalQuestions",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            viewModel.clearScheduledTime()
            viewModel.clearScore()

            onRestart()
        }) {
            Text(text = "Restart Quiz")
        }
    }
}