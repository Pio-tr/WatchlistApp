package com.example.watchlistapp

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.watchlistapp.screens.details.MovieDetailViewModel
import com.example.watchlistapp.screens.movies.MovieListViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire WatchlistApp
 */
object WatchlistViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WatchlistApplication
            MovieListViewModel(repository = application.repository)
        }

        initializer {
            val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WatchlistApplication
            MovieDetailViewModel(repository = application.repository)
        }
    }
}