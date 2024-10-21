package com.youmenotes.flagfindergame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.youmenotes.flagfindergame.ui.viewmodel.TimerScreenViewModel

@Composable
fun TimerScreen(
    onStartQuiz: (startedBefore: Int) -> Unit,
    resetFlag: Boolean = false,
    viewModel: TimerScreenViewModel = viewModel()
) {
    // Observe the remaining time and challenge start state
    val remainingTime by viewModel.remainingTime.collectAsState()
    val isChallengeStarted by viewModel.isChallengeStarted.collectAsState()

    var hour by remember { mutableIntStateOf(viewModel.hour) }
    var minute by remember { mutableIntStateOf(viewModel.minute) }
    var second by remember { mutableIntStateOf(viewModel.second) }

    val hours = (0..23).toList()
    val minutes = (0..59).toList()
    val seconds = (0..59).toList()

//    LaunchedEffect(Unit) {
//        if (resetFlag) {
//            viewModel.resetScheduledTime()
//        }
//    }

    LaunchedEffect(isChallengeStarted) {
        if (isChallengeStarted) {
            println("Starting Quiz")
            onStartQuiz(remainingTime)
            viewModel.resetTimer()
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
        // Title
        Text(
            text = "Set Challenge Time",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Time Picker
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hour Picker
            TimeDropdownMenu(label = "Hour", items = hours, selectedItem = hour) { selectedHour ->
                hour = selectedHour
            }
            Spacer(modifier = Modifier.width(8.dp))

            // Minute Picker
            TimeDropdownMenu(
                label = "Minute",
                items = minutes,
                selectedItem = minute
            ) { selectedMinute ->
                minute = selectedMinute
            }
            Spacer(modifier = Modifier.width(8.dp))

            // Second Picker
            TimeDropdownMenu(
                label = "Second",
                items = seconds,
                selectedItem = second
            ) { selectedSecond ->
                second = selectedSecond
            }
        }

        // Save Button
        Button(
            onClick = {
                viewModel.setTime(hour, minute, second)
                viewModel.saveScheduledTime()
                viewModel.startCountdown()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Save Time")
        }

        // Challenge Starting in 20 seconds Notification
        if (remainingTime in 1..20) {
            Text(
                text = "CHALLENGE WILL START IN 00:${String.format("%02d", remainingTime)}",
                color = Color.Red,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}

@Composable
fun TimeDropdownMenu(
    label: String,
    items: List<Int>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label)
        Box(
            modifier = Modifier
                .clickable { expanded = true }
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(8.dp)
        ) {
            Text(text = selectedItem.toString(), style = MaterialTheme.typography.bodyMedium)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(text = item.toString())
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
