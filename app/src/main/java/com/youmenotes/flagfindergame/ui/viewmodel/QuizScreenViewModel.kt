package com.youmenotes.flagfindergame.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.youmenotes.flagfindergame.data.model.Question
import com.youmenotes.flagfindergame.data.repository.RetrofitInstance
import com.youmenotes.flagfindergame.utils.MAX_QUESTION_COUNT
import com.youmenotes.flagfindergame.utils.QUESTION_COOL_DOWN_TIME
import com.youmenotes.flagfindergame.utils.QUESTION_COUNT_DOWN_TIME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class QuizScreenViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE)

    private val _quizData = mutableListOf<Question>()

    // StateFlow to hold the total questions
    private val _totalQuestions = MutableStateFlow(0)

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished = _isQuizFinished.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _correctAnswerId = MutableStateFlow<Int?>(null)

    private val _selectedAnswerId = MutableStateFlow<Int?>(null)
    val selectedAnswerId = _selectedAnswerId.asStateFlow()

    private val _remainingTime = MutableStateFlow(QUESTION_COUNT_DOWN_TIME)
    val remainingTime = _remainingTime.asStateFlow()

    private val _coolDownTime = MutableStateFlow(QUESTION_COOL_DOWN_TIME)
    val coolDownTime = _coolDownTime.asStateFlow()

    private var countdownJob: Job? = null
    private var cooldownJob: Job? = null

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = RetrofitInstance.fetchQuestions()
            when (result.isSuccess) {
                true -> {
                    _quizData.addAll(result.getOrDefault(emptyList()))
                    _totalQuestions.value = min(MAX_QUESTION_COUNT, _quizData.size)
                    if (_quizData.size == 0) {
                        _isLoading.value = false
                        _errorMessage.value = "No questions found"
                        return@launch
                    }
                    _isLoading.value = false
                    showNextQuestion()
                }

                false -> {
                    _isLoading.value = false
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Unknown error"
                }
            }
        }
    }

    private fun showNextQuestion() {
        if (_currentQuestionIndex.value < _totalQuestions.value) {
            _currentQuestion.value = _quizData[currentQuestionIndex.value]
            startQuestionTimer()
        } else {
            saveTotalQuestionCount()
            _isQuizFinished.value = true
        }
    }

    fun finishQuiz() {
        saveTotalQuestionCount()
        _isQuizFinished.value = true
    }

    fun saveTotalQuestionCount() {
        sharedPreferences.edit()
            .putInt("question_count", _totalQuestions.value)
            .apply()
    }

    private fun startQuestionTimer() {
        countdownJob?.cancel()
        cooldownJob?.cancel()
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
            val score = if (selectedAnswerId == currentQuestion.answerId) 1 else 0
            sharedPreferences.edit()
                .putInt("Q-${_currentQuestionIndex.value}", score)
                .apply()
        }
    }

    fun checkAnswer() {
        _remainingTime.value = 0
        _selectedAnswerId.value = sharedPreferences.getInt("$_currentQuestionIndex", -1)
        countdownJob?.cancel()
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (i in _coolDownTime.value downTo 0) {
                _coolDownTime.value = i
                delay(1000)
            }
            _currentQuestionIndex.value++
            resetCountdownTimers()
            showNextQuestion()
        }
    }

    private fun resetCountdownTimers() {
        _remainingTime.value = QUESTION_COUNT_DOWN_TIME
        _coolDownTime.value = QUESTION_COOL_DOWN_TIME
        _selectedAnswerId.value = -1
    }

    fun updateQuestion(startedBefore: Int) {
        val timeForOneQuestion = QUESTION_COUNT_DOWN_TIME + QUESTION_COOL_DOWN_TIME
        _currentQuestionIndex.value = -startedBefore / timeForOneQuestion
        val remainingCountDownTime = timeForOneQuestion - (-startedBefore % timeForOneQuestion)
        _remainingTime.value = remainingCountDownTime - QUESTION_COOL_DOWN_TIME
        _coolDownTime.value = min(remainingCountDownTime, QUESTION_COOL_DOWN_TIME)
        if (!_isLoading.value) {
            showNextQuestion()
        }

    }
}
