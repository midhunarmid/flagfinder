package com.youmenotes.flagfindergame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerScreenViewModel : ViewModel() {

    // Timer state (in seconds)
    private val _remainingTime = MutableStateFlow(20)
    val remainingTime = _remainingTime.asStateFlow()

    // State to indicate when the challenge should start
    private val _isChallengeStarted = MutableStateFlow(false)
    val isChallengeStarted = _isChallengeStarted.asStateFlow()

    // Function to start the countdown
    fun startCountdown() {
        viewModelScope.launch {
            for (i in 20 downTo 0) {
                _remainingTime.value = i
                delay(1000L) // 1-second delay
                println("Starting Quiz in ${_remainingTime.value}")
            }
            // After countdown finishes, start the challenge
            _isChallengeStarted.value = true
        }
    }

    fun resetTimer() {
        _isChallengeStarted.value = false
        _remainingTime.value = 20
    }
}
