package com.example.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz.model.QuizResponse
import com.example.quiz.repository.DataRepository
import com.example.quiz.ui.QuizsActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class QuizViewModel  @Inject constructor(private val repository: DataRepository) : ViewModel() {


    suspend fun getQuizList(mContext: QuizsActivity): Flow<QuizResponse> {
        return repository.getQuiz(mContext)
    }
}