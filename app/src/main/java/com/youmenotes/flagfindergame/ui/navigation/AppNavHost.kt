package com.youmenotes.flagfindergame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.youmenotes.flagfindergame.ui.screens.QuizScreen
import com.youmenotes.flagfindergame.ui.screens.ResultScreen
import com.youmenotes.flagfindergame.ui.screens.TimerScreen
import com.youmenotes.flagfindergame.ui.viewmodel.QuizScreenViewModel

// Define your routes for navigation
sealed class Screen(val route: String) {
    object Timer : Screen("timer_screen/{resetFlag}") {
        fun createRoute(resetFlag: Boolean) = "timer_screen/$resetFlag"
    }
    object Quiz : Screen("quiz_screen/{countdownTime}") {
        fun createRoute(countdownTime: Int) = "quiz_screen/$countdownTime"
    }

    object Result : Screen("result_screen")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Navigation graph for your screens
    NavHost(
        navController = navController,
        startDestination = Screen.Timer.route,
        modifier = modifier
    ) {
        // Timer screen composable
        composable(Screen.Timer.route) { navBackStackEntry ->
            val resetFlag =
                navBackStackEntry.arguments?.getString("resetFlag")?.toBoolean() ?: false

            TimerScreen(
                resetFlag = resetFlag,
                onStartQuiz = { startedBefore ->
                    // Navigate to quiz screen with countdown time as an argument
                    navController.navigate(Screen.Quiz.createRoute(startedBefore))
                }
            )
        }

        // Quiz screen composable
        composable(Screen.Quiz.route) { navBackStackEntry ->
            val startedBefore =
                navBackStackEntry.arguments?.getString("countdownTime")?.toIntOrNull() ?: 0

            QuizScreen(
                startedBefore = startedBefore,
                onQuizComplete = {
                    // Navigate to result screen when quiz ends
                    navController.navigate(Screen.Result.route)
                },
                onGoBack = {
                    navController.popBackStack()
                }
            )
        }

        // Result screen composable
        composable(Screen.Result.route) {
            ResultScreen(
                onRestart = {
                    // Navigate back to the timer screen to restart the game
                    navController.navigate(Screen.Timer.createRoute(true)) {
                        popUpTo(Screen.Timer.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
