package com.example.watchlistapp.screens.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchlistapp.database.WatchlistMovie
import com.example.watchlistapp.database.WatchlistRepository
import com.example.watchlistapp.model.MovieSearchResponse
import com.example.watchlistapp.retrofit.MovieType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.ceil

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(
        val movies: List<MovieSearchResponse.RemoteMovie>,
        val currentPage: Int,
        val totalPages: Int
    ) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

class MovieListViewModel(private val repository: WatchlistRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow<MovieType?>(null)
    val selectedType: StateFlow<MovieType?> = _selectedType.asStateFlow()

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        // Funkcja zmienia tylko tekst, nie dotyka stanu _searchUiState!
    }

    fun onTypeChange(newType: MovieType?) {
        _selectedType.value = newType
    }

    val watchlistMovies: StateFlow<List<WatchlistMovie>> = repository.allWatchlistMovies
        .combine(_selectedType) { movies, type ->
            if (type == null) {
                movies
            } else {
                movies.filter { it.type?.contains(type.name, ignoreCase = true) == true }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    private var currentQuery = ""
    private var currentPage = 1

    fun searchMovies() {
        val query = _searchQuery.value

        if (query.isBlank()) {
            _searchUiState.value = SearchUiState.Error("Wpisz tytuł produkcji przed wyszukiwaniem.")
            return
        }
        currentQuery = query
        currentPage = 1
        executeSearch()
    }

    fun clearSearch() {
        _searchQuery.value = ""
        currentQuery = ""
        _searchUiState.value = SearchUiState.Idle
    }

    fun nextPage() {
        val currentState = _searchUiState.value
        if (currentState is SearchUiState.Success && currentPage < currentState.totalPages) {
            currentPage++
            executeSearch()
        }
    }

    fun previousPage() {
        if (currentPage > 1) {
            currentPage--
            executeSearch()
        }
    }

    private fun executeSearch() {
        _searchUiState.value = SearchUiState.Loading
        val formattedQuery = prepareSearchQuery(currentQuery)

        repository.searchMovies(
            query = formattedQuery,
            type = _selectedType.value?.name,
            page = currentPage
        ).enqueue(object : Callback<MovieSearchResponse> {
            override fun onResponse(call: Call<MovieSearchResponse>, response: Response<MovieSearchResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null && body.response == "True" && body.searchResults != null) {
                    val totalResultsCount = body.totalResults?.toIntOrNull() ?: 0
                    val totalPages = ceil(totalResultsCount.toDouble() / 10.0).toInt()

                    _searchUiState.value = SearchUiState.Success(
                        movies = body.searchResults,
                        currentPage = currentPage,
                        totalPages = totalPages
                    )
                } else {
                    _searchUiState.value = SearchUiState.Error(body?.errorMessage ?: "Nie znaleziono produkcji.")
                }
            }

            override fun onFailure(call: Call<MovieSearchResponse>, t: Throwable) {
                _searchUiState.value = SearchUiState.Error("Błąd sieci: ${t.localizedMessage}")
            }
        })
    }

    private fun prepareSearchQuery(userInput: String): String {
        val cleanedInput = userInput.trim().replace("\\s+".toRegex(), " ")
        if (cleanedInput.isEmpty()) return ""
        return cleanedInput.split(" ")
            .joinToString(separator = " ") { word ->
                if (word.endsWith("*")) word else "$word*"
            }
    }
}