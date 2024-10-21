package com.youmenotes.flagfindergame.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ResultScreenViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE)


    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()
    private val _totalQuestions = MutableStateFlow(0)
    val totalQuestions = _totalQuestions.asStateFlow()

    init {
        getScoreData()
    }

    fun getScoreData() {
        println("getScoreData")
        _totalQuestions.value = sharedPreferences.getInt("question_count", 0)

        sharedPreferences.all.forEach {
            println("${it.key} : ${it.value}")
        }

        var calculatedScore = 0
        for (i in 0 until totalQuestions.value) {

            calculatedScore += sharedPreferences.getInt("Q-$i", 0)
        }

        _score.value = calculatedScore
    }

    fun clearPreferences() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}
