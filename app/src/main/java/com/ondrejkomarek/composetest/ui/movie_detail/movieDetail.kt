package com.ondrejkomarek.composetest.ui.movie_detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import androidx.lifecycle.SavedStateHandle
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.size.Scale
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

	/*Scaffold(
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
	}*/

	CollapsableToolbar(viewState)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollapsableToolbar(viewState: MovieDetailState) {
	val scrollState = rememberLazyListState()
	val swipingState = rememberSwipeableState(initialValue = SwipingStates.EXPANDED)

	BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

		val heightInPx = with(LocalDensity.current) { maxHeight.toPx() } // Get height of screen
		val connection = remember {
			object : NestedScrollConnection {

				override fun onPreScroll(
					available: Offset,
					source: NestedScrollSource
				): Offset {
					val delta = available.y
					return if(delta < 0) {
						swipingState.performDrag(delta).toOffset()
					} else {
						Offset.Zero
					}
				}

				override fun onPostScroll(
					consumed: Offset,
					available: Offset,
					source: NestedScrollSource
				): Offset {
					val delta = available.y
					return swipingState.performDrag(delta).toOffset()
				}

				override suspend fun onPostFling(
					consumed: Velocity,
					available: Velocity
				): Velocity {
					swipingState.performFling(velocity = available.y)
					return super.onPostFling(consumed, available)
				}

				private fun Float.toOffset() = Offset(0f, this)
			}
		}

		Box(
			modifier = Modifier
				.fillMaxSize()
				.swipeable(
					state = swipingState,
					thresholds = { _, _ -> FractionalThreshold(0.5f) },
					orientation = Orientation.Vertical,
					anchors = mapOf(
						// Maps anchor points (in px) to states
						0f to SwipingStates.COLLAPSED,
						heightInPx to SwipingStates.EXPANDED,
					)
				)
				.nestedScroll(connection)
		) {
			/*var animateToCollapsedState by remember { mutableStateOf(false) }
			val progress by animateFloatAsState(
				targetValue = if(animateToCollapsedState) 1f else 0f, // Based on boolean we change progress target // animateToEnd?? IDK
				animationSpec = tween(1000) // specifying animation type - Inbetweening animation with 1000ms duration
			)*/
			Column() {
				MotionComposeHeader(
					viewState.movieDetail?.posterUrl ?: "",
					viewState.movieDetail?.title ?: "",
					viewState.movieDetail?.releaseDate ?: "",
					viewState.movieDetail?.overview ?: "",
					progress = if (swipingState.progress.to == SwipingStates.COLLAPSED) swipingState.progress.fraction else 1f - swipingState.progress.fraction,
					swipingState = swipingState.currentValue
					) {
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
}

@Composable
fun MotionComposeHeader(posterUrl: String, movieTitle: String, releaseDate: String, movieOverview: String, progress: Float, swipingState: SwipingStates, scrollableBody: @Composable () -> Unit) {

	MotionLayout(
		start = JsonConstraintSetStart(),
		end = JsonConstraintSetEnd(),
		progress = progress,
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight(),
	) {

		Image(
			painter = rememberImagePainter(
				data = posterUrl,
				builder = {
					ImageRequest.Builder(LocalContext.current).transformations(
						RoundedCornersTransformation(12f)
					).scale(Scale.FIT)
				}
			),
			contentDescription = "Movie poster",
			modifier = Modifier
				.fillMaxWidth(1f)
				.wrapContentHeight()
				.layoutId("poster")
				.background(MaterialTheme.colors.primaryVariant),
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
			maxLines = when(swipingState) {
				SwipingStates.EXPANDED -> 3
				SwipingStates.COLLAPSED -> 1
			},
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier
				.layoutId("title")
				.wrapContentHeight()
				.fillMaxWidth(1f),
			color = motionColor("title", "textColor"), // Extracting color value from motionProperties
			fontSize = motionFontSize("title", "textSize"), // Extracting font size value from motionProperties
			style = MaterialTheme.typography.h6,
			textAlign = TextAlign.Start,

		)
		Box(
			Modifier
				.layoutId("content")
		) {
			Column() {
				CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
					Text(
						releaseDate,
						style = MaterialTheme.typography.body2
					)
				}
				Text(movieOverview, Modifier.wrapContentHeight(Alignment.Top))
				scrollableBody()
			}
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

// Helper class defining swiping State
enum class SwipingStates {
	EXPANDED,
	COLLAPSED
}

@Composable
private fun JsonConstraintSetStart() = ConstraintSet (""" {
	poster: { 
		width: "spread",
		height: "wrap", // switch to "packed" and back when on screen to fix this :O 
		start: ['parent', 'start', 0],
		end: ['parent', 'end', 0],
		top: ['parent', 'top', 0],
	},
	title: {
		top: ['poster', 'bottom', 16],
		start: ['parent', 'start', 16],
		custom: {
			textColor: "#000000", 
			textSize: 40
		},
	},
	content: {
		width: "spread",
		start: ['parent', 'start', 0],
		end: ['parent', 'end', 0],
		top: ['title', 'bottom', 16],
	}
} """ )

@Composable
private fun JsonConstraintSetEnd() = ConstraintSet (""" {
	poster: { 
		width: "spread",
		height: 56,
		start: ['parent', 'start', 0],
		end: ['parent', 'end', 0],
		top: ['parent', 'top', 0],
	},
	title: {
		top: ['parent', 'top', 0],
		start: ['parent', 'start', 16],
		end: ['parent', 'end', 0], 
		bottom: ['poster', 'bottom', 0],
		custom: {
			textColor: "#ffffff",
			textSize: 20
        },
	},
	content: {
		width: "spread",
		start: ['parent', 'start', 0],
		end: ['parent', 'end', 0],
		top: ['poster', 'bottom', 0],
	}
                  
} """)


@HiltViewModel
class MovieDetailViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val movieRepository: MovieRepository
) : BaseViewModel() {

	private val _state = MutableStateFlow(MovieDetailState())

	val state: StateFlow<MovieDetailState>
		get() = _state

	val movieName: String

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