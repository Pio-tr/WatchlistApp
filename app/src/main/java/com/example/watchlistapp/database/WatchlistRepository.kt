package com.example.watchlistapp.database

import com.example.watchlistapp.model.MovieDetailsResponse
import com.example.watchlistapp.model.MovieSearchResponse
import com.example.watchlistapp.retrofit.RetrofitClient
import kotlinx.coroutines.flow.Flow
import retrofit2.Call

class WatchlistRepository(private val watchlistDao: WatchlistDao) {

    val allWatchlistMovies: Flow<List<WatchlistMovie>> = watchlistDao.getAllMovies()

    suspend fun getMovieById(imdbId: String): WatchlistMovie? {
        return watchlistDao.getMovieById(imdbId)
    }

    suspend fun isMovieInWatchlist(imdbId: String): Boolean {
        return watchlistDao.isMovieInWatchlist(imdbId)
    }

    suspend fun insertMovie(movie: WatchlistMovie) {
        watchlistDao.insertMovie(movie)
    }

    suspend fun deleteMovie(movie: WatchlistMovie) {
        watchlistDao.deleteMovie(movie)
    }

    fun searchMovies(query: String, type: String?, page: Int): Call<MovieSearchResponse> {
        return RetrofitClient.apiServiceInstance.searchMovies(
            searchQuery = query,
            type = type,
            page = page
        )
    }

    fun getMovieDetails(imdbId: String): Call<MovieDetailsResponse> {
        return RetrofitClient.apiServiceInstance.getMovieDetails(imdbId)
    }
}