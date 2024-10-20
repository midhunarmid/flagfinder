package com.youmenotes.flagfindergame.data.repository

import com.youmenotes.flagfindergame.data.model.Question
import com.youmenotes.flagfindergame.data.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.jsonbin.io/" // Replace with your API base URL

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    suspend fun fetchQuestions() : Result<List<Question>> {
        return try {
            val response = api.getQuestions()
            println(response.body())

            if (response.isSuccessful) {
                Result.success(response.body()?.record?.questions ?: emptyList())
            } else {
                Result.failure(Exception(processErrorCodes(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun processErrorCodes(code: Int): String {
        return when (code) {
            400 -> "Bad Request"
            401 -> "Your request is un-authorized and can not be processed now!"
            403 -> "Forbidden"
            404 -> "Not Found"
            else -> "Your request has failed. Please try again later."
        }
    }
}