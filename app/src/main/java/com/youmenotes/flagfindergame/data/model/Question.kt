package com.youmenotes.flagfindergame.data.model
import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("answer_id") val answerId: Int,
    val countries: List<Country>,
    @SerializedName("country_code") val countryCode: String
)