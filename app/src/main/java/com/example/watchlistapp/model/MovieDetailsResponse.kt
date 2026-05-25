package com.example.watchlistapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieDetailsResponse(
    @Json(name = "Response") val response: String,
    @Json(name = "Error") val errorMessage: String?,
    @Json(name = "Title") val title: String?,
    @Json(name = "Year") val year: String?,
    @Json(name = "imdbID") val imdbId: String?,
    @Json(name = "Poster") val posterUrl: String?,
    @Json(name = "Plot") val plot: String?,
    @Json(name = "Genre") val genre: String?,
    @Json(name = "Type") val type: String?,
    @Json(name = "Runtime") val runtime: String?,
    @Json(name = "imdbRating") val imdbRating: String?
)