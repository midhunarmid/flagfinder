package com.youmenotes.flagfindergame.data.model

import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("country_name") val countryName: String,
    val id: Int
)