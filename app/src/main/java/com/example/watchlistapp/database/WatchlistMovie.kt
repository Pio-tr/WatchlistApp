package com.example.watchlistapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_table")
data class WatchlistMovie(
    @PrimaryKey
    val imdbId: String,
    val title: String,
    val year: String,
    val posterUrl: String,
    val plot: String?,
    val genre: String?,
    val type: String?,
    val runtime: String?,
    val imdbRating: String?
)