package com.example.watchlistapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM watchlist_table ORDER BY title ASC")
    fun getAllMovies(): Flow<List<WatchlistMovie>>

    @Query("SELECT * FROM watchlist_table WHERE imdbId = :imdbId LIMIT 1")
    suspend fun getMovieById(imdbId: String): WatchlistMovie?

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_table WHERE imdbId = :imdbId)")
    suspend fun isMovieInWatchlist(imdbId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: WatchlistMovie)

    @Delete
    suspend fun deleteMovie(movie: WatchlistMovie)
}