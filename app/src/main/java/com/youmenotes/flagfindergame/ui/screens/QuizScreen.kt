package com.youmenotes.flagfindergame.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.youmenotes.flagfindergame.ui.viewmodel.QuizScreenViewModel

@Composable
fun QuizScreen(
    quizViewModel: QuizScreenViewModel,
    onQuizComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentQuestion by quizViewModel.currentQuestion.collectAsState()
    val totalQuestions = quizViewModel.totalQuestions.collectAsState()
    val errorMessage = quizViewModel.errorMessage.collectAsState()

    // Check if the quiz is complete
    if (quizViewModel.isQuizFinished.collectAsState(initial = false).value) {
        onQuizComplete()
    } else {
        // Show a loading indicator while fetching questions
        if (totalQuestions.value == 0 && errorMessage.value.isEmpty()) {
            CircularProgressIndicator()
        } else if (errorMessage.value.isNotEmpty()) {
            // Show error message to the user
            Text(text = "Error: ${errorMessage.value}", color = Color.Red)
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Question ${quizViewModel.currentQuestionIndex.collectAsState(initial = 0).value + 1} of $totalQuestions",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                currentQuestion?.let { question ->
                    AsyncImage(
                        model = "https://flagcdn.com/256x192/${question.countryCode.lowercase()}.png",
                        contentDescription = "Flag",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                currentQuestion?.countries?.forEach { country ->
                    // Display answer options
                    Text(
                        text = country.countryName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                quizViewModel.verifyAnswer(country.id)
                            },
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

    }
}
