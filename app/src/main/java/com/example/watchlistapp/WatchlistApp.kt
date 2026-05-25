package com.example.watchlistapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.watchlistapp.database.WatchlistDatabase
import com.example.watchlistapp.database.WatchlistRepository
import com.example.watchlistapp.screens.details.MovieDetailScreen
import com.example.watchlistapp.screens.details.MovieDetailViewModel
import com.example.watchlistapp.screens.movies.MovieListScreen
import com.example.watchlistapp.screens.movies.MovieListViewModel

@Composable
fun WatchlistApp() {
    val context = LocalContext.current

    val database = WatchlistDatabase.getDatabase(context)
    WatchlistViewModelFactory.repository = WatchlistRepository(database.watchlistDao())

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "movie_list_screen"
    ) {
        composable("movie_list_screen") {
            val listViewModel: MovieListViewModel = viewModel(factory = WatchlistViewModelFactory)
            MovieListScreen(
                viewModel = listViewModel,
                onMovieClick = { imdbId ->
                    navController.navigate("movie_detail_screen/$imdbId")
                }
            )
        }

        composable(
            route = "movie_detail_screen/{imdbId}",
            arguments = listOf(navArgument("imdbId") { type = NavType.StringType })
        ) { backStackEntry ->
            val imdbId = backStackEntry.arguments?.getString("imdbId") ?: ""
            val detailViewModel: MovieDetailViewModel = viewModel(factory = WatchlistViewModelFactory)

            MovieDetailScreen(
                imdbId = imdbId,
                viewModel = detailViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}