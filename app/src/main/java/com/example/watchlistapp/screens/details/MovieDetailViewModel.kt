package com.example.watchlistapp.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchlistapp.R
import com.example.watchlistapp.database.WatchlistMovie
import com.example.watchlistapp.database.WatchlistRepository
import com.example.watchlistapp.model.MovieDetailsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val movieDetails: MovieDetailsResponse) : DetailUiState

    data class Error(val titleRes: Int, val dynamicMessage: String? = null) : DetailUiState
}

class MovieDetailViewModel(private val repository: WatchlistRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _isInWatchlist = MutableStateFlow(false)
    val isInWatchlist: StateFlow<Boolean> = _isInWatchlist.asStateFlow()

    fun loadMovieDetails(imdbId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            val isSaved = repository.isMovieInWatchlist(imdbId)
            _isInWatchlist.value = isSaved

            val localMovie = repository.getMovieById(imdbId)

            if (localMovie != null && !localMovie.plot.isNullOrBlank()) {
                _uiState.value = DetailUiState.Success(
                    MovieDetailsResponse(
                        response = "True",
                        errorMessage = null,
                        title = localMovie.title,
                        year = localMovie.year,
                        imdbId = localMovie.imdbId,
                        posterUrl = localMovie.posterUrl,
                        plot = localMovie.plot,
                        genre = localMovie.genre,
                        type = localMovie.type,
                        runtime = localMovie.runtime,
                        imdbRating = localMovie.imdbRating
                    )
                )
            } else {
                fetchFromNetwork(imdbId)
            }
        }
    }

    private fun fetchFromNetwork(imdbId: String) {
        repository.getMovieDetails(imdbId).enqueue(object : Callback<MovieDetailsResponse> {
            override fun onResponse(call: Call<MovieDetailsResponse>, response: Response<MovieDetailsResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null && body.response == "True") {
                    _uiState.value = DetailUiState.Success(body)
                } else {
                    if (body?.errorMessage != null) {
                        _uiState.value = DetailUiState.Error(
                            titleRes = R.string.error_detail_api_failure,
                            dynamicMessage = body.errorMessage
                        )
                    } else {
                        _uiState.value = DetailUiState.Error(titleRes = R.string.error_detail_unknown)
                    }
                }
            }

            override fun onFailure(call: Call<MovieDetailsResponse>, t: Throwable) {
                _uiState.value = DetailUiState.Error(
                    titleRes = R.string.error_detail_network_failure,
                    dynamicMessage = t.localizedMessage
                )
            }
        })
    }

    fun toggleWatchlist(movieDetails: MovieDetailsResponse) {
        viewModelScope.launch {
            val imdbId = movieDetails.imdbId ?: return@launch

            if (_isInWatchlist.value) {
                val movieToDelete = WatchlistMovie(imdbId = imdbId, title = "", year = "", posterUrl = "", type = "", plot = "", genre = "", runtime = "", imdbRating = "")
                repository.deleteMovie(movieToDelete)
                _isInWatchlist.value = false
            } else {
                val fullMovieToSave = WatchlistMovie(
                    imdbId = imdbId,
                    title = movieDetails.title ?: "",
                    year = movieDetails.year ?: "",
                    posterUrl = movieDetails.posterUrl ?: "",
                    plot = movieDetails.plot,
                    genre = movieDetails.genre,
                    type = movieDetails.type,
                    runtime = movieDetails.runtime,
                    imdbRating = movieDetails.imdbRating
                )
                repository.insertMovie(fullMovieToSave)
                _isInWatchlist.value = true
            }
        }
    }
}
