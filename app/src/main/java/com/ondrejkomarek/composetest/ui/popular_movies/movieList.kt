package com.ondrejkomarek.composetest.ui.popular_movies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.navigation.AppNavigationScreens
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
				MoviesApp(viewModel, {
					// TODO
				})
			}
		}
	}
}

@Composable
fun MoviesApp(
	viewModel: MoviesViewModel,
	onMovieClick: (Int) -> Unit
) {
	val allScreens = AppNavigationScreens.values().toList()
	/*
	Need to import manually
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
	 */
	var currentScreen by rememberSaveable { mutableStateOf(AppNavigationScreens.PopularMovies) }
	val navController = rememberNavController()
	Movies(viewModel, onMovieClick)
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
				backgroundColor = MaterialTheme.colors.primaryVariant,
				actions = {
					IconButton(onClick = { /* doSomething() */ }) {
						Icon(Icons.Filled.Favorite, contentDescription = null)
					}
				}
			)
		}
	) { innerPadding ->
		Column() {
			LazyColumn(state = scrollState) {
				items(movies.size) {
					MovieCard(movies[it], onMovieClick)
				}
			}
		}
	}
}

@ExperimentalCoilApi
@Composable // for better reusability
fun MovieCard(movieItem: Movie, onMovieClick: (Int) -> Unit, modifier: Modifier = Modifier) {
	Card(
		backgroundColor = MaterialTheme.colors.surface,
		modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
	){
		Row(
			modifier = modifier
				.clickable(onClick = { onMovieClick(movieItem.id) })
				.padding(16.dp)
				.fillMaxWidth(1f)
		) {
			Surface(
				modifier = Modifier
					.sizeIn(maxHeight = 120.dp),
				color = MaterialTheme.colors.onSurface,
				shape = RoundedCornerShape(12.dp)
			) {
				Image(
					painter = rememberImagePainter(
						data = movieItem.posterUrl,
						builder = { ImageRequest.Builder(LocalContext.current).transformations(RoundedCornersTransformation(12f)) }
					),
					contentDescription = "Movie poster",
					modifier = Modifier
						.sizeIn(minHeight = 120.dp)
						.fillMaxHeight(),
				)
			}
			Column(
				modifier = Modifier
					.padding(start = 16.dp)
					.align(Alignment.Top)
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


