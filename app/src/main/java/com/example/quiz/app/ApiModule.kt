package com.example.quiz.app

import com.example.quiz.repository.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {


    @Provides
    @Singleton
    fun bindsAppRepo(): DataRepository {
        return DataRepository()
    }
}