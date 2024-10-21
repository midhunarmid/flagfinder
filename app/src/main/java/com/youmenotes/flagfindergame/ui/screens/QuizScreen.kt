package com.youmenotes.flagfindergame.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.youmenotes.flagfindergame.R
import com.youmenotes.flagfindergame.ui.viewmodel.QuizScreenViewModel
import com.youmenotes.flagfindergame.ui.widgets.MyButton

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
    var showExitDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.updateQuestion(startedBefore)
    }

    LaunchedEffect(isQuizFinished) {
        if (isQuizFinished) {
            onQuizComplete()
        }
    }

    BackHandler {
        // Show confirmation dialog when back is pressed
        showExitDialog = true
    }

    if (showExitDialog) {
        ExitConfirmationDialog(onConfirm = {
            showExitDialog = false
        }, onDismiss = {
            showExitDialog = false
        })
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

            MyButton(stringResource(R.string.go_back)) { onGoBack() }
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
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)
            ) {
                if (remainingTime > 0) {
                    CountDownTimerDisplay(remainingTime)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.your_clock_is_ticking),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    CountDownTimerDisplay(coolDownTime, Color.LightGray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.you_are_in_cool_down_time),
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

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .clickable {
                        if (remainingTime != 0) {
                            viewModel.registerAnswer(country.id)
                        }
                    }
                    .background(
                        bgColor, shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    if (selectedAnswerId == country.id) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Check",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Display country name and feedback (correct/wrong)
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = country.countryName, style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Show correct/wrong feedback after time is up
                        if (remainingTime == 0) {
                            if (country.id == currentQuestion?.answerId) {
                                Text(
                                    text = stringResource(R.string.correct_answer),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            } else if (country.id == selectedAnswerId) {
                                Text(
                                    text = stringResource(R.string.wrong_answer),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            MyButton(stringResource(R.string.finish_quiz)) { viewModel.finishQuiz() }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun QuestionHeader(questionNumber: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
        ) {
            Text(
                text = questionNumber.toString(), color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(R.string.guess_the_country_by_the_flag),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
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
            .size(80.dp, 40.dp)
            .background(color)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = formattedTime, color = Color.White, fontSize = 20.sp)
    }
}

@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.complete_quiz)) },
        text = { Text(text = stringResource(R.string.you_can_not_exit_the_challenge_halfway_the_timer_is_still_ticking)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = stringResource(R.string.ok))
            }
        },
    )
}