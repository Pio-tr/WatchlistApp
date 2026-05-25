package com.example.watchlistapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.watchlistapp.database.WatchlistRepository
import com.example.watchlistapp.screens.details.MovieDetailViewModel
import com.example.watchlistapp.screens.movies.MovieListViewModel

object WatchlistViewModelFactory : ViewModelProvider.Factory {
    lateinit var repository: WatchlistRepository

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MovieListViewModel::class.java) ->
                MovieListViewModel(repository) as T
            modelClass.isAssignableFrom(MovieDetailViewModel::class.java) ->
                MovieDetailViewModel(repository) as T
            else -> throw IllegalArgumentException("Nieznana klasa ViewModelu: ${modelClass.name}")
        }
    }
}