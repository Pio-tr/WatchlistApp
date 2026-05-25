package com.example.watchlistapp.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    imdbId: String,
    viewModel: MovieDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isInWatchlist by viewModel.isInWatchlist.collectAsState()

    LaunchedEffect(imdbId) {
        viewModel.loadMovieDetails(imdbId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.Success -> {
                    val movie = state.movieDetails
                    val fallbackText = stringResource(R.string.not_available)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .align(Alignment.CenterHorizontally),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            AsyncImage(
                                model = movie.posterUrl,
                                contentDescription = stringResource(R.string.poster_description),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit,
                                error = painterResource(R.drawable.ic_placeholder_poster)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = movie.title ?: stringResource(R.string.no_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = stringResource(R.string.movie_year, movie.year ?: fallbackText),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.toggleWatchlist(movie) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isInWatchlist) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = if (isInWatchlist) stringResource(R.string.watchlist_remove) else stringResource(R.string.watchlist_add)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.movie_genre, movie.genre ?: fallbackText),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // NOWA SEKCA: Typ, Czas trwania, Ocena IMDb
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. TYP (np. MOVIE / SERIES)
                            SuggestionChip(
                                onClick = { },
                                label = { Text((movie.type ?: fallbackText).uppercase()) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    labelColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            // 2. RUNTIME (Czas trwania)
                            Text(
                                text = stringResource(R.string.movie_runtime, movie.runtime ?: fallbackText),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )

                            // 3. IMDB RATING (Ocena)
                            Text(
                                text = stringResource(R.string.movie_rating, movie.imdbRating ?: fallbackText),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.plot_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = movie.plot ?: stringResource(R.string.no_plot),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                is DetailUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}