package com.ondrejkomarek.composetest.ui.popular_movies

import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.navigation.AppNavigationScreens
import com.ondrejkomarek.composetest.network.MovieRepository
import com.ondrejkomarek.composetest.ui.BaseViewModel
import com.ondrejkomarek.composetest.ui.movie_detail.MovieDetail
import com.ondrejkomarek.composetest.utility.Failure
import com.ondrejkomarek.composetest.utility.ScreenState
import com.ondrejkomarek.composetest.utility.fold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoilApi
@Composable // for better reusability
fun PopularMovies(viewModel: PopularMoviesViewModel, onMovieClick: (Int) -> Unit) {
	val viewState = viewModel.state.collectAsState().value
	val scrollState = rememberLazyListState()

	Column() {
		LazyColumn(state = scrollState) {
			items(viewState.movies.size) {
				MovieListCard(viewState.movies[it], onMovieClick)
			}
		}
	}
}

@ExperimentalCoilApi
@Composable // for better reusability
fun MovieListCard(movieItem: Movie, onMovieClick: (Int) -> Unit, modifier: Modifier = Modifier) {
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
class PopularMoviesViewModel @Inject constructor(
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


