package com.example.watchlistapp.screens.movies

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.watchlistapp.R
import com.example.watchlistapp.database.WatchlistMovie
import com.example.watchlistapp.model.MovieSearchResponse
import com.example.watchlistapp.retrofit.MovieType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel,
    onMovieClick: (String) -> Unit
) {
    val watchlistMovies by viewModel.watchlistMovies.collectAsState()
    val searchUiState by viewModel.searchUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()

    Scaffold(
        topBar = {
            val isSearching = searchUiState !is SearchUiState.Idle

            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                navigationIcon = {
                    if (isSearching) {
                        // Strzałka jako JEDYNA metoda powrotu do bazy danych
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back_description)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // --- SEKCE STATYCZNE ---
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.search_placeholder)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            // ZMIANA: Kliknięcie X teraz TYLKO czyści tekst, nie resetuje stanu ekranu
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.clear_description)
                                )
                            }
                        }
                    }
                )
            }

            item {
                Text(text = stringResource(R.string.production_type), style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { viewModel.onTypeChange(null) }) {
                        RadioButton(selected = (selectedType == null), onClick = { viewModel.onTypeChange(null) })
                        Text(stringResource(R.string.type_all), modifier = Modifier.padding(start = 4.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { viewModel.onTypeChange(MovieType.movie) }) {
                        RadioButton(selected = (selectedType == MovieType.movie), onClick = { viewModel.onTypeChange(MovieType.movie) })
                        Text(stringResource(MovieType.movie.displayName), modifier = Modifier.padding(start = 4.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { viewModel.onTypeChange(MovieType.series) }) {
                        RadioButton(selected = (selectedType == MovieType.series), onClick = { viewModel.onTypeChange(MovieType.series) })
                        Text(stringResource(MovieType.series.displayName), modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.searchMovies() },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(stringResource(R.string.search_button))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            when (val state = searchUiState) {
                is SearchUiState.Idle -> {
                    if (watchlistMovies.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.watchlist_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.watchlist_header, watchlistMovies.size),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        items(watchlistMovies) { movie ->
                            LocalMovieItem(movie = movie, onClick = { onMovieClick(movie.imdbId) })
                        }
                    }
                }

                is SearchUiState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is SearchUiState.Success -> {
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.search_results_header),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    items(state.movies) { remoteMovie ->
                        RemoteMovieItem(movie = remoteMovie, onClick = { onMovieClick(remoteMovie.imdbId) })
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = { viewModel.previousPage() }, enabled = state.currentPage > 1) {
                                Text(stringResource(R.string.page_previous))
                            }
                            Text(stringResource(R.string.page_counter, state.currentPage, state.totalPages))
                            Button(onClick = { viewModel.nextPage() }, enabled = state.currentPage < state.totalPages) {
                                Text(stringResource(R.string.page_next))
                            }
                        }
                    }
                }

                is SearchUiState.Error -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val fullErrorMessage = if (state.dynamicMessage != null) {
                                stringResource(id = state.titleRes, state.dynamicMessage)
                            } else {
                                stringResource(id = state.titleRes)
                            }

                            Text(
                                text = fullErrorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocalMovieItem(movie: WatchlistMovie, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = null,
                modifier = Modifier.size(60.dp, 90.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_placeholder_poster)
            )
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.movie_year, movie.year), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun RemoteMovieItem(movie: MovieSearchResponse.RemoteMovie, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = null,
                modifier = Modifier.size(60.dp, 90.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_placeholder_poster)
            )
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.movie_year, movie.year), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}