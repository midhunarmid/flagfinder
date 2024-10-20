package com.youmenotes.flagfindergame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youmenotes.flagfindergame.data.model.Question
import com.youmenotes.flagfindergame.data.repository.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizScreenViewModel :ViewModel() {

    private val _quizData = mutableListOf<Question>()


    // StateFlow to hold the total questions
    private val _totalQuestions = MutableStateFlow(0)
    val totalQuestions = _totalQuestions.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished = _isQuizFinished.asStateFlow()

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            val result = RetrofitInstance.fetchQuestions()
            when (result.isSuccess) {
                true -> {
                    _quizData.addAll(result.getOrDefault(emptyList()))
                    _totalQuestions.value = _quizData.size
                    if (_quizData.size == 0) {
                        _errorMessage.value = "No questions found"
                        return@launch
                    }
                    _currentQuestion.value = _quizData[0]
                }
                false -> {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Unknown error"
                }
            }
        }
    }

    fun verifyAnswer(selectedAnswerId: Int) {
        val currentQuestion = _currentQuestion.value
        if (currentQuestion != null && currentQuestion.answerId == selectedAnswerId) {
            _score.value += 1
        }
        moveToNextQuestion()
    }

    private fun moveToNextQuestion() {
        val nextIndex = _currentQuestionIndex.value + 1
        if (nextIndex < _quizData.size) {
            _currentQuestionIndex.value = nextIndex
            _currentQuestion.value = _quizData[nextIndex] // Get next question from local data
        } else {
            _isQuizFinished.value = true // Quiz finished
        }
    }
}
