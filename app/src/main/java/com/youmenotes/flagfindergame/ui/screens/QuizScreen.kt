package com.youmenotes.flagfindergame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.youmenotes.flagfindergame.ui.viewmodel.QuizScreenViewModel

@Composable
fun QuizScreen(
    viewModel: QuizScreenViewModel = viewModel(),
    startedBefore: Int,
    onQuizComplete: () -> Unit,
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit,
) {
    val currentQuestion by viewModel.currentQuestion.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedAnswerId by viewModel.selectedAnswerId.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()
    val coolDownTime by viewModel.coolDownTime.collectAsState()
    val isQuizFinished by viewModel.isQuizFinished.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateQuestion(startedBefore)
    }

    LaunchedEffect(isQuizFinished) {
        if (isQuizFinished) {
            onQuizComplete()
        }
    }

    // Show a loading indicator while fetching questions
    if (isLoading) {
        CircularProgressIndicator()
    } else if (errorMessage.isNotEmpty()) {
        // Show error message to the user
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Error: $errorMessage", color = Color.Red)

            Button(onClick = onGoBack) {
                Text(text = "Go Back")
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            QuestionHeader(currentQuestionIndex + 1)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                if (remainingTime > 0) {
                    CountDownTimerDisplay(remainingTime)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Your clock is ticking...",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    CountDownTimerDisplay(coolDownTime, Color.LightGray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "You are in cool down time...",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

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

                val bgColor = if (remainingTime == 0) {
                    when (country.id) {
                        currentQuestion?.answerId -> {
                            Color.Green
                        }

                        selectedAnswerId -> {
                            Color.LightGray
                        }

                        else -> {
                            MaterialTheme.colorScheme.surface
                        }
                    }
                } else {
                    MaterialTheme.colorScheme.surface
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .clickable {
                            if (remainingTime != 0) {
                                viewModel.registerAnswer(country.id)
                            }
                        }
                        .background(
                            bgColor,
                            shape = RoundedCornerShape(8.dp)
                        ) // Rounded background
                        .padding(16.dp), // Padding inside the row
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedAnswerId == country.id) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Flag",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp)) // Space between the icon and text

                    // Display country name and feedback (correct/wrong)
                    Column(
                        modifier = Modifier.weight(1f) // Ensures that the text takes the remaining width
                    ) {
                        Text(
                            text = country.countryName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Show correct/wrong feedback after time is up
                        if (remainingTime == 0) {
                            if (country.id == currentQuestion?.answerId) {
                                Text(
                                    text = "Correct Answer",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            } else if (country.id == selectedAnswerId) {
                                Text(
                                    text = "Wrong Answer",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }
            }

            if (remainingTime != 0) {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        viewModel.checkAnswer()

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally) // Align button at the bottom
                        .padding(16.dp), // Padding to avoid screen edges
                ) {
                    Text(
                        text = "Check Answer",
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.finishQuiz()

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally) // Align button at the bottom
                    .padding(16.dp), // Padding to avoid screen edges
            ) {
                Text(
                    text = "Finish Quiz",
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )

            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun QuestionHeader(questionNumber: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        // Circle with question number
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
        ) {
            Text(
                text = questionNumber.toString(),
                color = Color.White, // Set the text color
                style = MaterialTheme.typography.headlineMedium // Adjust text style as needed
            )
        }

        Spacer(modifier = Modifier.width(8.dp)) // Add space between the circle and text

        // "Guess the Country by the Flag" text
        Text(
            text = "Guess the Country by the Flag",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground // Use onBackground for contrast
        )
    }
}

@Composable
fun CountDownTimerDisplay(remainingTime: Int, color: Color = Color.Black) {
    val minutes = remainingTime / 60
    val seconds = remainingTime % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .size(80.dp, 40.dp) // Set the size of the box
            .background(color)
            .padding(8.dp), // Padding inside the box
        contentAlignment = Alignment.Center // Center text inside the box
    ) {
        Text(text = formattedTime, color = Color.White, fontSize = 20.sp) // White text
    }
}