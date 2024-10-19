package com.youmenotes.flagfindergame.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.youmenotes.flagfindergame.ui.viewmodel.TimerScreenViewModel

@Composable
fun TimerScreen(
    onStartQuiz: () -> Unit,
    viewModel: TimerScreenViewModel = viewModel()
) {
    // Observe the remaining time and challenge start state
    val remainingTime by viewModel.remainingTime.collectAsState()
    val isChallengeStarted by viewModel.isChallengeStarted.collectAsState()


    // Use LaunchedEffect to react to changes in isChallengeStarted
    LaunchedEffect(isChallengeStarted) {
        if (isChallengeStarted) {
            println("Starting Quiz")
            onStartQuiz()  // Call onStartQuiz only once when challenge starts
        }
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the countdown
        Text(text = "Challenge will start in $remainingTime seconds")

        Spacer(modifier = Modifier.height(16.dp))

        // Start Challenge Button
        Button(onClick = { viewModel.startCountdown() }) {
            Text(text = "Start Challenge")
        }
    }
}
