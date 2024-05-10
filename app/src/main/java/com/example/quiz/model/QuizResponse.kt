package com.example.quiz.model

import com.google.gson.annotations.SerializedName

data class QuizResponse(

	@field:SerializedName("questions")
	val questions: List<QuestionsItem?>? = null
)

data class CountriesItem(

	@field:SerializedName("country_name")
	val countryName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class QuestionsItem(

	@field:SerializedName("country_code")
	val countryCode: String? = null,

	@field:SerializedName("countries")
	val countries: List<CountriesItem?>? = null,

	@field:SerializedName("answer_id")
	val answerId: Int? = null
)
