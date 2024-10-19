package com.youmenotes.flagfindergame.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.youmenotes.flagfindergame.data.repository.RetrofitInstance

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideQuizRepository(): RetrofitInstance {
        // Return an instance of QuizRepository (or a mock for testing)
        return RetrofitInstance
    }
}
