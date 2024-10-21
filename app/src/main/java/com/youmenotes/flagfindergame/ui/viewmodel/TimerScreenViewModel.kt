package com.youmenotes.flagfindergame.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE)

    // Timer state (in seconds)
    private val _remainingTime = MutableStateFlow(20)
    val remainingTime = _remainingTime.asStateFlow()

    // State to indicate when the challenge should start
    private val _isChallengeStarted = MutableStateFlow(false)
    val isChallengeStarted = _isChallengeStarted.asStateFlow()

    private var countdownJob: Job? = null

    var hour by mutableIntStateOf(0)
    var minute by mutableIntStateOf(0)
    var second by mutableIntStateOf(0)

    init {
        loadScheduledTime()
    }

    fun setTime(h: Int, m: Int, s: Int) {
        hour = h
        minute = m
        second = s
    }


    // Logic to save the time
    fun saveScheduledTime() {
        sharedPreferences.edit()
            .putInt("scheduled_hour", hour)
            .putInt("scheduled_minute", minute)
            .putInt("scheduled_second", second)
            .apply()
        loadScheduledTime()
    }

    // Load scheduled time from SharedPreferences
    fun loadScheduledTime() {
        hour = sharedPreferences.getInt("scheduled_hour", -1)
        minute = sharedPreferences.getInt("scheduled_minute", -1)
        second = sharedPreferences.getInt("scheduled_second", -1)
        println("Saved time is ${hour}:${minute}:${second}")
        startCountdown()
    }

    // Function to start the countdown
    fun startCountdown() {
        // Cancel the previous countdown if it is still running
        countdownJob?.cancel()

        if (hour == -1 || minute == -1 || second == -1) {
            println("Time not set")
            return
        }

        // Start a new countdown
        countdownJob = viewModelScope.launch {
            calculateRemainingTime()
            if (_remainingTime.value > 0) {
                // Start the countdown with the remaining seconds
                for (i in _remainingTime.value downTo 0) {
                    _remainingTime.value = i
                    delay(1000L) // 1-second delay
                    println("Starting Quiz in ${_remainingTime.value} seconds")
                }
                // After countdown finishes, start the challenge
                _isChallengeStarted.value = true
            } else {
                // If the time has already passed, start the challenge immediately
                _isChallengeStarted.value = true
            }
        }
    }

    fun resetTimer() {
        _isChallengeStarted.value = false
        calculateRemainingTime()
    }

    private fun calculateRemainingTime() {
        // Get the current system time
        val currentTimeMillis = System.currentTimeMillis()

        // Create a Calendar object for the scheduled time
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
        }

        // Calculate the remaining time in seconds
        val scheduledTimeMillis = calendar.timeInMillis
        val remainingMillis = scheduledTimeMillis - currentTimeMillis
        _remainingTime.value = (remainingMillis / 1000).toInt()
    }
}
