package com.youmenotes.flagfindergame.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.youmenotes.flagfindergame.R
import com.youmenotes.flagfindergame.ui.viewmodel.ResultScreenViewModel
import com.youmenotes.flagfindergame.ui.widgets.MyButton

@Composable
fun ResultScreen(
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResultScreenViewModel = viewModel()
) {
    val score by viewModel.score.collectAsState()
    val totalQuestions by viewModel.totalQuestions.collectAsState()

    // Handle back button press using BackHandler
    BackHandler {
        viewModel.clearPreferences()
        onRestart()
    }

    // Display the result screen layout
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.quiz_completed),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.your_score, score, totalQuestions),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        MyButton(stringResource(R.string.restart_quiz)) {
            viewModel.clearPreferences()
            onRestart()
        }
    }
}