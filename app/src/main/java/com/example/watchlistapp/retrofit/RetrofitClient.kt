package com.example.watchlistapp.retrofit

import androidx.annotation.StringRes
import com.example.watchlistapp.R
import com.example.watchlistapp.model.MovieDetailsResponse
import com.example.watchlistapp.model.MovieSearchResponse
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

enum class MovieType(@StringRes val displayName: Int) {
    movie(R.string.type_movie),
    series(R.string.type_series)
}

object RetrofitClient {
    val apiServiceInstance: ApiService by lazy {
        getClient().create(ApiService::class.java)
    }

    private const val BASE_URL = "https://www.omdbapi.com/"

    private fun getClient(): Retrofit {
        val moshi = Moshi.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit
    }
}

interface ApiService {
    @GET("/")
    fun searchMovies(
        @Query("s") searchQuery: String,
        @Query("type") type: String? = null,
        @Query("page") page: Int? = null,
        @Query("apikey") apiKey: String = "515f103d"
    ): Call<MovieSearchResponse>

    @GET("/")
    fun getMovieDetails(
        @Query("i") imdbId: String,
        @Query("apikey") apiKey: String = "515f103d",
        @Query("plot") plot: String = "full"
    ): Call<MovieDetailsResponse>
}