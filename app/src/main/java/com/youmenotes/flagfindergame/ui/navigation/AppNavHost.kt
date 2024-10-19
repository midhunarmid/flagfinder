package com.youmenotes.flagfindergame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.youmenotes.flagfindergame.ui.screens.TimerScreen
import com.youmenotes.flagfindergame.ui.screens.QuizScreen
import com.youmenotes.flagfindergame.ui.screens.ResultScreen
import com.youmenotes.flagfindergame.ui.viewmodel.QuizScreenViewModel

// Define your routes for navigation
sealed class Screen(val route: String) {
    object Timer : Screen("timer_screen")
    object Quiz : Screen("quiz_screen")
    object Result : Screen("result_screen")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    quizViewModel: QuizScreenViewModel,
    modifier: Modifier = Modifier
) {
    // Navigation graph for your screens
    NavHost(
        navController = navController,
        startDestination = Screen.Timer.route,
        modifier = modifier
    ) {
        // Timer screen composable
        composable(Screen.Timer.route) {
            TimerScreen(
                onStartQuiz = {
                    // Navigate to quiz screen when the timer ends
                    navController.navigate(Screen.Quiz.route)
                }
            )
        }

        // Quiz screen composable
        composable(Screen.Quiz.route) {
            QuizScreen(
                quizViewModel = quizViewModel,
                onQuizComplete = {
                    // Navigate to result screen when quiz ends
                    navController.navigate(Screen.Result.route)
                }
            )
        }

        // Result screen composable
        composable(Screen.Result.route) {
            ResultScreen(
                quizViewModel = quizViewModel,
                onRestart = {
                    // Navigate back to the timer screen to restart the game
                    navController.navigate(Screen.Timer.route) {
                        popUpTo(Screen.Timer.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
