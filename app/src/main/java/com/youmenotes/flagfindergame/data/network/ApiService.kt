package com.youmenotes.flagfindergame.data.network

import com.youmenotes.flagfindergame.data.model.QuestionResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("v3/b/671234e4ad19ca34f8ba9eff")
    suspend fun getQuestions(): Response<QuestionResponse>
}