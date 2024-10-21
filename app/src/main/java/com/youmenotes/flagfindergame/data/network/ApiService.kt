package com.youmenotes.flagfindergame.data.network

import com.youmenotes.flagfindergame.data.model.QuestionResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("v3/b/67140dc9ad19ca34f8bb546d")
    suspend fun getQuestions(): Response<QuestionResponse>
}