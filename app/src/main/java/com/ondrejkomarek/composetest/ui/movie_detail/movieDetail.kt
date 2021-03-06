package com.ondrejkomarek.composetest.ui.movie_detail

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Handler
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.NonNull
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.MotionLayout
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
import com.ondrejkomarek.composetest.ui.universal.EmptyState
import com.ondrejkomarek.composetest.ui.universal.MyCircularProgressIndicator
import com.ondrejkomarek.composetest.utility.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.tlaster.nestedscrollview.VerticalNestedScrollView
import moe.tlaster.nestedscrollview.rememberNestedScrollViewState
import javax.inject.Inject

@ExperimentalCoilApi
@Composable // for better reusability
fun MovieDetail(viewModel: MovieDetailViewModel) {
	val viewState = viewModel.state.collectAsState().value

	val videoProgress = rememberSaveable { mutableStateOf(0f) }
	val isFullscreen = rememberSaveable { mutableStateOf(false) }

	val configuration = LocalConfiguration.current
	val screenHeight = configuration.screenHeightDp.dp
	val screenWidth = configuration.screenWidthDp.dp

	BackHandler(enabled = isFullscreen.value) {
		isFullscreen.value = false
	}

	when(viewState.state) {
		ScreenState.CONTENT -> {
			if(screenHeight >= screenWidth) {
				CollapsableToolbar(
					isFullscreen,
					videoProgress,
					viewState
				)
			} else {
				MovieDetailContainer(isFullscreen, videoProgress, viewState)
			}
		}
		ScreenState.PROGRESS -> MyCircularProgressIndicator()
		ScreenState.EMPTY -> EmptyState()
	}

	//NestedTest()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NestedTest() {
	val nestedScrollViewState = rememberNestedScrollViewState()
	VerticalNestedScrollView(
		state = nestedScrollViewState,
		header = {
			Surface(
				modifier = Modifier
					.fillMaxWidth(),
				color = MaterialTheme.colors.primary,
			) {
				Column(
					modifier = Modifier.padding(16.dp)
				) {
					Text(text = "This is some awesome title")
					Text(text = "This is some awesome title")
					Text(text = "This is some awesome title")
					Text(text = "This is some awesome title")
					Text(text = "This is some awesome title")
					Text(text = "This is some awesome title")
					Text(text = "This is some awesome title")
					Text(text = "This is some awesome title")
					Text(text = "This is some awesome title")
					Text(text = "This is some awesome title")
					Text(text = "This is some awesome title")
				}
			}
		},
		content = {
			val pages = (0..4).map { it }
			Column {
				TabRow(
					selectedTabIndex = 0,
					indicator = { tabPositions ->

					}
				) {
					pages.forEachIndexed { index, title ->
						Tab(
							text = { Text(text = "tab $title") },
							selected = false,
							onClick = {}
						)
					}
				}

				LazyColumn {
					items(100) {
						ListItem {
							Text(text = "item $it")
						}
					}
				}
			}
		}
	)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollapsableToolbar(
	isFullscreen: MutableState<Boolean>,
	videoProgress: MutableState<Float>,
	viewState: MovieDetailState
) {
	val swipingState = rememberSwipeableState(initialValue = SwipingStates.EXPANDED)

	BoxWithConstraints(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colors.surface)
	) {

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
			Column() {
				MotionComposeHeader(
					viewState.movieDetail?.posterUrl ?: "",
					viewState.movieDetail?.title ?: "",
					viewState.movieDetail?.releaseDate ?: "",
					viewState.movieDetail?.overview ?: "",
					progress = if(swipingState.progress.to == SwipingStates.COLLAPSED) swipingState.progress.fraction else 1f - swipingState.progress.fraction,
					swipingState = swipingState.currentValue
				) {
					MovieDetailContainer(isFullscreen, videoProgress, viewState)
				}
			}
		}
	}
}


@Composable
fun MotionComposeHeader(
	posterUrl: String,
	movieTitle: String,
	releaseDate: String,
	movieOverview: String,
	progress: Float,
	swipingState: SwipingStates,
	scrollableBody: @Composable () -> Unit
) {
	val configuration = LocalConfiguration.current
	val screenHeight = configuration.screenHeightDp.dp
	val screenWidth = configuration.screenWidthDp.dp

	MotionLayout(
		start = JsonConstraintSetStart(),
		end = JsonConstraintSetEnd(),
		progress = progress,
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight()
		//.wrapContentHeight(),
	) {

		Image(
			painter = rememberImagePainter(
				data = posterUrl,
				builder = {
					ImageRequest.Builder(LocalContext.current).transformations(
						RoundedCornersTransformation(12f)
					)
						.scale(Scale.FIT)
						//.size(OriginalSize) // Need this to force loading when not having fixed height.
						.placeholder(R.drawable.ic_launcher_background)
				}
			),
			contentDescription = "Movie poster",
			modifier = Modifier
				.fillMaxWidth(1f)
				//.height(screenWidth / 2 * 3)
				.wrapContentHeight(Alignment.Top, true)//Alignment.Top, true)
				.defaultMinSize(Dp.Hairline, 300.dp)
				//.height(IntrinsicSize.Max)
				.layoutId("poster")
				.background(MaterialTheme.colors.primaryVariant),
			contentScale = ContentScale.FillWidth,
			alpha = 1f - progress
		)

		/*GlideImage(
			imageModel = posterUrl,
			// Crop, Fit, Inside, FillHeight, FillWidth, None
			contentScale = ContentScale.Fit,
			// shows an image with a circular revealed animation.
			circularReveal = CircularReveal(duration = 250),
			alpha = 1f - progress,
			modifier = Modifier
				.fillMaxWidth(1f)
				//.wrapContentHeight(Alignment.Top, false)//Alignment.Top, true)
				//.defaultMinSize(Dp.Hairline, 1.dp)
				.layoutId("poster")
				.background(MaterialTheme.colors.primaryVariant),
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
			color = Color.White,
			fontSize = motionFontSize(
				"title",
				"textSize"
			), // Extracting font size value from motionProperties
			style = MaterialTheme.typography.h6.copy(
				shadow = Shadow(
					color = motionColor(
						"title",
						"shadowColor"
					),// Extracting color value from motionProperties
					offset = Offset(4f, 4f),
					blurRadius = 8f
				)
			),
			textAlign = TextAlign.Start,

			)
		Box(
			Modifier
				.layoutId("content")
		) {
			Column() {
				scrollableBody()
			}
		}
	}
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MovieDetailContainer(
	isFullscreen: MutableState<Boolean>,
	videoProgress: MutableState<Float>,
	viewState: MovieDetailState
) {
	val configuration = LocalConfiguration.current
	val screenHeight = configuration.screenHeightDp.dp
	val screenWidth = configuration.screenWidthDp.dp

	Surface(
		modifier = Modifier.fillMaxSize(),
		color = MaterialTheme.colors.surface
	) {

		if(screenHeight >= screenWidth) {
			MovieDetailContent(
				isFullscreen,
				videoProgress,
				viewState,
				viewState.movieDetail?.actors
			)
		} else {
			MovieDetailContentLandscape(
				isFullscreen,
				videoProgress,
				viewState,
				viewState.movieDetail?.actors
			)
		}
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MovieDetailContent(
	isFullscreen: MutableState<Boolean>,
	videoProgress: MutableState<Float>,
	viewState: MovieDetailState,
	actorList: List<Actor>?
) {
	val scrollState = rememberLazyListState()
	val nestedScrollViewState = rememberNestedScrollViewState()
	VerticalNestedScrollView(
		modifier = Modifier.wrapContentHeight(),
		state = nestedScrollViewState,
		header = {
			Column(
				modifier = Modifier
					.fillMaxWidth()
			) {
				val releaseDate = viewState.movieDetail?.releaseDate ?: ""
				val movieOverview = viewState.movieDetail?.overview ?: ""

				if(!isFullscreen.value) {
					OverviewText(null, releaseDate, movieOverview)
				}

				viewState.movieDetail?.videoId?.let { videoId ->
					Box(if(isFullscreen.value) Modifier.fillMaxSize() else Modifier) {
						YoutubePlayer(isFullscreen.value, videoId, videoProgress) {
							isFullscreen.value = it
						}
					}
				}
			}
		},
		content = {
			actorList?.let { actors ->
				LazyColumn {
					if(!isFullscreen.value) {
						items(actors.size) {
							ActorListItem(actors[it])
						}
					}
				}
			}
		}
	)
	// TODO youtube player is crashing in NestedScroll now when put to fullscreen, maybe newer version of compose will fix it.

	/*Column() {

		actorList?.let { actors ->
			LazyColumn(state = scrollState) {
				item {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.wrapContentHeight()
					) {
						val releaseDate = viewState.movieDetail?.releaseDate ?: ""
						val movieOverview = viewState.movieDetail?.overview ?: ""

						Column() {

							if(!isFullscreen.value) {
								OverviewText(null, releaseDate, movieOverview)
							}

							viewState.movieDetail?.videoId?.let { videoId ->
								Box(if(isFullscreen.value) Modifier.fillMaxSize() else Modifier) {
									YoutubePlayer(isFullscreen.value, videoId, videoProgress) {
										isFullscreen.value = it
									}
								}
							}
						}
					}
				}

				if(!isFullscreen.value) {
					items(actors.size) {
						ActorListItem(actors[it])
					}
				}
			}
		}
	}*/
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MovieDetailContentLandscape(
	isFullscreen: MutableState<Boolean>,
	videoProgress: MutableState<Float>,
	viewState: MovieDetailState,
	actorList: List<Actor>?
) {
	val scrollState = rememberLazyListState()
	val nestedScrollViewState = rememberNestedScrollViewState()
	VerticalNestedScrollView(
		modifier = Modifier.wrapContentHeight(),
		state = nestedScrollViewState,
		header = {
			val movieTitle = viewState.movieDetail?.title ?: ""
			val releaseDate = viewState.movieDetail?.releaseDate ?: ""
			val posterUrl = viewState.movieDetail?.posterUrl ?: ""
			val movieOverview = viewState.movieDetail?.overview ?: ""

			Column() {

				viewState.movieDetail?.videoId?.let { videoId ->
					Box(if(isFullscreen.value) Modifier else Modifier) {
						YoutubePlayer(isFullscreen.value, videoId, videoProgress) {
							isFullscreen.value = it
						}
					}
				}
				Row() {

					if(!isFullscreen.value) {
						Image(
							painter = rememberImagePainter(
								data = posterUrl,
								builder = {
									ImageRequest.Builder(LocalContext.current)
										.transformations(
											RoundedCornersTransformation(12f),

											)
										.scale(Scale.FILL)
								}
							),
							contentDescription = "Movie poster",
							modifier = Modifier
								.height(200.dp)
								.layoutId("poster")
								.padding(start = 16.dp, top = 24.dp),
							contentScale = ContentScale.FillHeight
						)

						OverviewText(movieTitle, releaseDate, movieOverview)
					}
				}
			}
		},
		content = {
			actorList?.let { actors ->
				LazyColumn {
					if(!isFullscreen.value) {
						items(actors.size) {
							ActorListItem(actors[it])
						}
					}
				}
			}
		}
	)


	 /*
	Column() {

		actorList?.let { actors ->
			LazyColumn(state = scrollState) {
				item {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.wrapContentHeight()
					) {
						val movieTitle = viewState.movieDetail?.title ?: ""
						val releaseDate = viewState.movieDetail?.releaseDate ?: ""
						val posterUrl = viewState.movieDetail?.posterUrl ?: ""
						val movieOverview = viewState.movieDetail?.overview ?: ""

						Column() {

							viewState.movieDetail?.videoId?.let { videoId ->
								Box(if(isFullscreen.value) Modifier else Modifier) {
									YoutubePlayer(isFullscreen.value, videoId, videoProgress) {
										isFullscreen.value = it
									}
								}
							}
							Row() {

								if(!isFullscreen.value) {
									Image(
										painter = rememberImagePainter(
											data = posterUrl,
											builder = {
												ImageRequest.Builder(LocalContext.current)
													.transformations(
														RoundedCornersTransformation(12f),

														)
													.scale(Scale.FILL)
											}
										),
										contentDescription = "Movie poster",
										modifier = Modifier
											.height(200.dp)
											.layoutId("poster")
											.padding(start = 16.dp, top = 24.dp),
										contentScale = ContentScale.FillHeight
									)

									OverviewText(movieTitle, releaseDate, movieOverview)
								}
							}
						}
					}
				}

				if(!isFullscreen.value) {

					items(actors.size) {
						ActorListItem(actors[it])
					}
				}
			}
		}
	}*/
}

@Composable
fun OverviewText(movieTitle: String?, releaseDate: String, movieOverview: String) {
	Column(Modifier.padding(start = 16.dp, end = 16.dp)) {
		Spacer(modifier = Modifier.padding(top = 24.dp))

		movieTitle?.let {
			Text(
				text = movieTitle ?: "",
				modifier = Modifier
					.layoutId("title")
					.wrapContentHeight()
					.fillMaxWidth(1f),
				style = MaterialTheme.typography.h6,
				textAlign = TextAlign.Start,

				)
		}

		CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
			Text(
				releaseDate,
				style = MaterialTheme.typography.body2
			)
		}
		Text(
			movieOverview,
			Modifier
				.wrapContentHeight(Alignment.Top)
				.padding(top = 8.dp, bottom = 16.dp)
		)
	}
}

@Composable
fun YoutubePlayer(
	shouldBeFullScreen: Boolean,
	videoId: String,
	progressSeconds: MutableState<Float>,
	fullscreenCallback: (Boolean) -> Unit
) {
	val activity = LocalContext.current as Activity
	val lifecycle = LocalLifecycleOwner.current.lifecycle
	val fullscreenHelper = rememberSaveable { FullScreenHelper() }
	val configuration = LocalConfiguration.current
	val screenHeight = configuration.screenHeightDp.dp
	val screenWidth = configuration.screenWidthDp.dp

	val youTubePlayer = remember(activity) {
		YouTubePlayerView(activity).apply {
			addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
				override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
					youTubePlayer.cueVideo(videoId, progressSeconds.value)
				}

				override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
					progressSeconds.value = second
				}

				override fun onStateChange(
					youTubePlayer: YouTubePlayer,
					state: PlayerConstants.PlayerState
				) {
					Log.d("onStateChange", "onStateChange: ${state.name}")
					super.onStateChange(youTubePlayer, state)
				}
			})

			addFullScreenListener(object : YouTubePlayerFullScreenListener {
				override fun onYouTubePlayerEnterFullScreen() {
					activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
					fullscreenHelper.enterFullScreen(activity)
					Log.d("asd", "onYouTubePlayerEnterFullScreen")
					fullscreenCallback(true)
					//minimumHeight = screenHeight.toPx.toInt()
					//minimumWidth = screenWidth.toPx.toInt()
				}

				override fun onYouTubePlayerExitFullScreen() {
					activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
					fullscreenHelper.exitFullScreen(activity)
					Log.d("asd", "onYouTubePlayerExitFullScreen")
					fullscreenCallback(false)
					minimumHeight = 0
					minimumWidth = 0
				}
			})
		}
	}

	lifecycle.addObserver(youTubePlayer)

	AndroidView(
		modifier = if(shouldBeFullScreen) Modifier.height(
			screenHeight + Dp(
				activity.getStatusBarHeight().toFloat().toDp
			)
		) else Modifier,
		factory = { youTubePlayer }
	)

	if(youTubePlayer.isFullScreen() && shouldBeFullScreen.not()) {
		val handler =
			Handler() // TODO needed delay here, because when switching from port to land other youtube player was not ready for this.
		handler.postDelayed({
			youTubePlayer.exitFullScreen()
		}, 300)
	} else if(youTubePlayer.isFullScreen().not() && shouldBeFullScreen) {
		val handler = Handler()
		handler.postDelayed({
			youTubePlayer.enterFullScreen()
		}, 300)
	}
}

@Composable // for better reusability
fun ActorListItem(actor: Actor, modifier: Modifier = Modifier) {
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

// Helper class defining swiping State
enum class SwipingStates {
	EXPANDED,
	COLLAPSED
}

@Composable
private fun JsonConstraintSetStart() = ConstraintSet(
	""" {
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
			shadowColor: "#FF000000",
			textSize: 40
		},
	},
	content: {
		width: "spread",
		start: ['parent', 'start', 0],
		end: ['parent', 'end', 0],
		top: ['title', 'bottom', 16],
	}
} """
)

@Composable
private fun JsonConstraintSetEnd() = ConstraintSet(
	""" {
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
			shadowColor: "#00000000",
			textSize: 20
        },
	},
	content: {
		width: "spread",
		start: ['parent', 'start', 0],
		end: ['parent', 'end', 0],
		top: ['poster', 'bottom', 0],
	}
                  
} """
)


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
		movieName =
			requireNotNull(savedStateHandle.get<String>(movieNameArg)) { "Movie name is null" }
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