package com.youmenotes.flagfindergame.data.model

data class QuizState(
    val questions: List<Question>? = null,
    val currentQuestion: Question? = null,
    val currentQuestionNumber: Int = 0,
    val totalQuestions: Int = 15,
    val timeRemaining: Int = 30,      // Time remaining for the current question
    val isAnswered: Boolean = false,  // Whether the current question has been answered
    val isCorrect: Boolean = false,   // Whether the selected answer is correct
    val isQuizFinished: Boolean = false  // Whether the quiz is over
)
