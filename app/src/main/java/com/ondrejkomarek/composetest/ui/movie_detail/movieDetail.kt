package com.ondrejkomarek.composetest.ui.movie_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.network.MovieRepository
import com.ondrejkomarek.composetest.ui.BaseViewModel
import com.ondrejkomarek.composetest.ui.movieIdArg
import com.ondrejkomarek.composetest.ui.popular_movies.MovieListCard
import com.ondrejkomarek.composetest.ui.popular_movies.PopularMoviesViewModel
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
fun MovieDetail(viewModel: MovieDetailViewModel) {
	val viewState = viewModel.state.collectAsState().value
	val scrollState = rememberLazyListState()

	Column(Modifier.fillMaxWidth(1f)) {
		Surface(
			color = MaterialTheme.colors.onSurface,
			shape = RoundedCornerShape(12.dp),
			modifier = Modifier.fillMaxWidth(1f)
		) {
			Image(
				painter = rememberImagePainter(
					data = viewState.movie?.posterUrl,
					builder = { ImageRequest.Builder(LocalContext.current).transformations(
						RoundedCornersTransformation(12f)
					) }
				),
				contentDescription = "Movie poster",
				modifier = Modifier
					.fillMaxWidth(1f),
			)
		}
		Column(
			modifier = Modifier
				.fillMaxWidth(1f)
				.padding(start = 16.dp, end = 16.dp)
				.align(Alignment.CenterHorizontally)
		) {
			Text(viewState.movie?.title?: "", fontWeight = FontWeight.Bold)
			// LocalContentAlpha is defining opacity level of its children
			CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
				Text(viewState.movie?.releaseDate?: "", style = MaterialTheme.typography.body2)
			}
		}
	}
}
/*
@ExperimentalCoilApi
@Composable // for better reusability
fun MovieDetail(movieItem: Movie, onMovieClick: (Int) -> Unit, modifier: Modifier = Modifier) {
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

		}
	}
}*/


@HiltViewModel
class MovieDetailViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val movieRepository: MovieRepository
) : BaseViewModel() {

	private val _state = MutableStateFlow(MovieDetailState())

	val state: StateFlow<MovieDetailState>
		get() = _state

	init {
		launch {
			val movieId = requireNotNull(savedStateHandle.get<Int>(movieIdArg)) { "Movie id is null" }
			loadMovie(movieId)
		}
	}

	private suspend fun loadMovie(movieId: Int) = movieRepository.getMovieDetail(movieId).fold(::handleFailure, ::handleMovieList)

	private fun handleFailure(failure: Failure) {
		_state.value = MovieDetailState(
			state = ScreenState.EMPTY,
			movie = null
		)
	}

	private fun handleMovieList(movieDetail: Movie) {
		_state.value = MovieDetailState(
			state = ScreenState.CONTENT,
			movie = movieDetail
		)
	}
}