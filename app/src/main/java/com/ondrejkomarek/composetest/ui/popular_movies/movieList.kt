package com.ondrejkomarek.composetest.ui.popular_movies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.network.MovieRepository
import com.ondrejkomarek.composetest.ui.BaseViewModel
import com.ondrejkomarek.composetest.ui.theme.ComposeTestTheme
import com.ondrejkomarek.composetest.utility.Failure
import com.ondrejkomarek.composetest.utility.ScreenState
import com.ondrejkomarek.composetest.utility.fold
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MovieListActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val viewModel: MoviesViewModel by viewModels()

		setContent {
			ComposeTestTheme {
				// A surface container using the 'background' color from the theme
				Surface(color = MaterialTheme.colors.background) {
					Movies(viewModel, {
						// TODO
					})
				}
			}
		}
	}
}

@Composable
fun Movies(
	viewModel: MoviesViewModel,
	onMovieClick: (Int) -> Unit
) {
	val viewState = viewModel.state.collectAsState().value

	MoviesContent(
		viewState.state,
		viewState.movies,
		onMovieClick
	)
}

@Composable
fun MoviesContent(state: ScreenState, movies: List<Movie>, onMovieClick: (Int) -> Unit) {
	val scrollState = rememberLazyListState()

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(text = "Popular movies")
				},
				actions = {
					IconButton(onClick = { /* doSomething() */ }) {
						Icon(Icons.Filled.Favorite, contentDescription = null)
					}

					IconButton(onClick = { /* doSomething() */ }) {
						Icon(Icons.Filled.DeleteForever, contentDescription = "Delete")
					}
				}
			)
		}
	) { innerPadding ->
		Column() {
			Text(text = "Hi there!", modifier = Modifier.padding(innerPadding))
			LazyColumn(state = scrollState) {
				items(movies.size) {
					MovieCard(movies[it])
				}
			}
		}
	}
}

@ExperimentalCoilApi
@Composable // for better reusability
fun MovieCard(movieItem: Movie, modifier: Modifier = Modifier) {
	Row(
		modifier = modifier
			.clickable(onClick = { /* Ignoring onClick */ })
			.padding(16.dp)
	) {
		Surface(
			modifier = Modifier.size(56.dp),
			shape = CircleShape,
			color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
		) {
			Image(
				painter = rememberImagePainter(
					data = movieItem.posterUrl
				),
				contentDescription = "Movie poster",
				modifier = Modifier.size(50.dp)
			)
		}
		Column(
			modifier = Modifier
				.padding(start = 16.dp)
				.align(Alignment.CenterVertically)
		) {
			Text(movieItem.title, fontWeight = FontWeight.Bold)
			// LocalContentAlpha is defining opacity level of its children
			CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
				Text(movieItem.releaseDate, style = MaterialTheme.typography.body2)
			}
		}
	}
}

@HiltViewModel
class MoviesViewModel @Inject constructor(
	private val movieRepository: MovieRepository
) : BaseViewModel() {

	// Holds our view state which the UI collects via [state]
	private val _state = MutableStateFlow(PopularMoviesListState())

	val state: StateFlow<PopularMoviesListState>
		get() = _state

	init {
		launch {
			loadMovies()
		}
	}

	private suspend fun loadMovies() = movieRepository.getMovies().fold(::handleFailure, ::handleMovieList)

	private fun handleFailure(failure: Failure) {
		_state.value = PopularMoviesListState(
			state = ScreenState.EMPTY,
			movies = emptyList()
		)
	}

	private fun handleMovieList(movies: List<Movie>) {
		_state.value = PopularMoviesListState(
			state = ScreenState.CONTENT,
			movies = movies
		)
	}
}


