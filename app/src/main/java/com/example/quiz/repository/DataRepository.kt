package com.example.quiz.repository


import com.example.quiz.model.QuizResponse
import com.example.quiz.ui.QuizsActivity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DataRepository @Inject constructor()  {

    /**
     * This API returns movie in realtime using which stats can be computed
     */
    suspend fun getQuiz(mContext: QuizsActivity): Flow<QuizResponse>  {
        return flow {
            val jsonString =
                mContext.assets.open("Quiz.json").bufferedReader()
                    .use { it.readText() }
            val gson = Gson()
            emit(gson.fromJson(jsonString, QuizResponse::class.java))
        }.flowOn(Dispatchers.IO)
    }


}
