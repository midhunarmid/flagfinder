package com.youmenotes.flagfindergame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youmenotes.flagfindergame.data.model.Question
import com.youmenotes.flagfindergame.data.repository.RetrofitInstance
import com.youmenotes.flagfindergame.utils.QUESTION_COOL_DOWN_TIME
import com.youmenotes.flagfindergame.utils.QUESTION_COUNT_DOWN_TIME
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizScreenViewModel : ViewModel() {

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

    private val _correctAnswerId = MutableStateFlow<Int?>(null)
    val correctAnswerId = _correctAnswerId.asStateFlow()

    private val _selectedAnswerId = MutableStateFlow<Int?>(null)
    val selectedAnswerId = _selectedAnswerId.asStateFlow()

    private val _remainingTime = MutableStateFlow(30)
    val remainingTime = _remainingTime.asStateFlow()

    private val _coolDownTime = MutableStateFlow(30)
    val coolDownTime = _coolDownTime.asStateFlow()

    private var countdownJob: Job? = null


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
                    _currentQuestionIndex.value = 0
                    showNextQuestion()
                }

                false -> {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Unknown error"
                }
            }
        }
    }

    private fun showNextQuestion() {
        if (_currentQuestionIndex.value < _quizData.size) {
            _currentQuestion.value = _quizData[currentQuestionIndex.value]
            startQuestionTimer()
        } else {
            // Quiz is complete, handle completion
        }
    }

    private fun startQuestionTimer(countDownTime: Int = QUESTION_COUNT_DOWN_TIME, coolDownTime: Int = QUESTION_COOL_DOWN_TIME) {
        _remainingTime.value = countDownTime
        _coolDownTime.value = coolDownTime

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (i in _remainingTime.value downTo 0) {
                _remainingTime.value = i
                delay(1000)
            }
            checkAnswer()
        }
    }

    fun registerAnswer(selectedAnswerId: Int) {
        val currentQuestion = _currentQuestion.value
        if (currentQuestion != null) {
            _correctAnswerId.value = currentQuestion.answerId
            _selectedAnswerId.value = selectedAnswerId
        }
    }

    fun checkAnswer() {
        _remainingTime.value = 0
        _coolDownTime.value = QUESTION_COOL_DOWN_TIME

        countdownJob?.cancel()
        viewModelScope.launch {
            for (i in _coolDownTime.value downTo 0) {
                _coolDownTime.value = i
                delay(1000)
            }
            _currentQuestionIndex.value++
            showNextQuestion()
        }
    }

    fun updateQuestion(startedBefore: Int) {
        _currentQuestionIndex.value = -startedBefore / (QUESTION_COUNT_DOWN_TIME + QUESTION_COOL_DOWN_TIME)
        showNextQuestion()

        val remainingCountDownTime = -startedBefore % (QUESTION_COUNT_DOWN_TIME + QUESTION_COOL_DOWN_TIME)
        startQuestionTimer(countDownTime = remainingCountDownTime)
    }
}
