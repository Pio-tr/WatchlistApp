package com.example.watchlistapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieSearchResponse(
    @Json(name = "Search") val searchResults: List<RemoteMovie>?,
    @Json(name = "Response") val response: String,
    @Json(name = "Error") val errorMessage: String?,
    @Json(name = "totalResults") val totalResults: String?
) {
    @JsonClass(generateAdapter = true)
    data class RemoteMovie(
        @Json(name = "Title") val title: String,
        @Json(name = "Year") val year: String,
        @Json(name = "imdbID") val imdbId: String,
        @Json(name = "Type") val type: String,
        @Json(name = "Poster") val posterUrl: String
    )
}