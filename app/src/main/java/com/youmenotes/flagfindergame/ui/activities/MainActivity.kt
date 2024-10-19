package com.youmenotes.flagfindergame.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.youmenotes.flagfindergame.ui.navigation.AppNavHost
import com.youmenotes.flagfindergame.ui.theme.FlagFinderGameTheme
import com.youmenotes.flagfindergame.ui.viewmodel.QuizScreenViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val quizViewModel = QuizScreenViewModel()
            FlagFinderGameTheme {
                val navController = rememberNavController()
                QuizApp(navController = navController, quizViewModel = quizViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizApp(navController: NavHostController, quizViewModel: QuizScreenViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Flags Challenge") }
            )
        },
        content = { innerPadding ->
            AppNavHost(
                navController = navController,
                quizViewModel = quizViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlagFinderGameTheme {
        // You can add some previewable UI here if needed
    }
}