package com.ondrejkomarek.composetest.ui.movie_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import androidx.lifecycle.SavedStateHandle
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.ondrejkomarek.composetest.R
import com.ondrejkomarek.composetest.model.Actor
import com.ondrejkomarek.composetest.model.MovieDetail
import com.ondrejkomarek.composetest.network.MovieRepository
import com.ondrejkomarek.composetest.ui.BaseViewModel
import com.ondrejkomarek.composetest.ui.movieIdArg
import com.ondrejkomarek.composetest.ui.movieNameArg
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

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(text = viewModel.movieName, maxLines = 1, overflow = TextOverflow.Ellipsis)
				},
				backgroundColor = MaterialTheme.colors.primaryVariant,
				actions = {
					IconButton(onClick = { /* doSomething() */ }) {
						Icon(Icons.Outlined.Favorite, contentDescription = null)
					}
				}
			)
		}
	) { innerPadding ->

		Column(Modifier.fillMaxWidth(1f)) {
			Surface(
				color = MaterialTheme.colors.onSurface,
				shape = RoundedCornerShape(12.dp),
				modifier = Modifier
					.fillMaxWidth(1f)
					.padding(innerPadding)
			) {
				Image(
					painter = rememberImagePainter(
						data = viewState.movieDetail?.posterUrl,
						builder = {
							ImageRequest.Builder(LocalContext.current).transformations(
								RoundedCornersTransformation(12f)
							)
						}
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
				Text(viewState.movieDetail?.title ?: "", fontWeight = FontWeight.Bold)
				// LocalContentAlpha is defining opacity level of its children
				CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
					Text(
						viewState.movieDetail?.releaseDate ?: "",
						style = MaterialTheme.typography.body2
					)
				}
				viewState.movieDetail?.overview?.let {
					Text(it)
				}
				Text("Actors:", fontWeight = FontWeight.Bold)
				viewState.movieDetail?.actors?.let { actors ->
					LazyColumn(state = scrollState) {
						items(actors.size) {
							ActorList(actors[it])
						}
					}
				}
			}
		}
	}


}

@Composable
fun MotionComposeHeader(posterUrl: String, movieTitle: String, movieOverview: String, progress: Float, scrollableBody: @Composable () -> Unit) {

	MotionLayout(
		//start = //TODO,
		//end = //TODO,
		progress = progress,
		modifier = Modifier
			.fillMaxWidth(),
		motionScene = MotionScene(content = "") // TODO
	) {

		Image(
			painter = rememberImagePainter(
				data = posterUrl,
				builder = {
					ImageRequest.Builder(LocalContext.current).transformations(
						RoundedCornersTransformation(12f)
					)
				}
			),
			contentDescription = "Movie poster",
			modifier = Modifier
				.fillMaxWidth(1f)
				.layoutId("poster")
				.background(MaterialTheme.colors.primary),
			contentScale = ContentScale.FillWidth,
			alpha = 1f - progress
			)
		/*Image(
			painter = painterResource(id = R.drawable.poster),
			contentDescription = "poster",
			modifier = Modifier
				.layoutId("poster")
				.background(MaterialTheme.colors.primary),
			contentScale = ContentScale.FillWidth,
			alpha = 1f - progress // Update alpha based on progress. Expanded -> 1f / Collapsed -> 0f (transparent)
		)*/
		Text(
			text = movieTitle,
			modifier = Modifier
				.layoutId("title")
				.wrapContentHeight(),
			style = MaterialTheme.typography.h6,
			textAlign = TextAlign.Center
		)
		Box(
			Modifier
				.layoutId("content")
		) {
			scrollableBody()
		}
	}
}

@Composable // for better reusability
fun ActorList(actor: Actor, modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
			.padding(16.dp)
			.fillMaxWidth(1f)
	) {
		Text(actor.name, fontWeight = FontWeight.Bold)
		// LocalContentAlpha is defining opacity level of its children
		CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
			Text(actor.character, style = MaterialTheme.typography.body2)
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

	lateinit var movieName: String

	init {
		val movieId = requireNotNull(savedStateHandle.get<Int>(movieIdArg)) { "Movie id is null" }
		movieName = requireNotNull(savedStateHandle.get<String>(movieNameArg)) { "Movie name is null" }
		launch {
			loadMovie(movieId)
		}
	}

	private suspend fun loadMovie(movieId: Int) =
		movieRepository.getMovieDetail(movieId).fold(::handleFailure, ::handleMovieList)

	private fun handleFailure(failure: Failure) {
		_state.value = MovieDetailState(
			state = ScreenState.EMPTY,
			movieDetail = null
		)
	}

	private fun handleMovieList(movieDetail: MovieDetail) {
		_state.value = MovieDetailState(
			state = ScreenState.CONTENT,
			movieDetail = movieDetail
		)
	}
}