package com.ondrejkomarek.composetest.ui.popular_movies

import android.graphics.drawable.Drawable
import android.os.Handler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.annotation.ExperimentalCoilApi
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.ondrejkomarek.composetest.R
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.network.MovieRepository
import com.ondrejkomarek.composetest.ui.BaseViewModel
import com.ondrejkomarek.composetest.ui.MoviesActivity
import com.ondrejkomarek.composetest.ui.universal.EmptyState
import com.ondrejkomarek.composetest.ui.universal.LocalThemeToggle
import com.ondrejkomarek.composetest.ui.universal.MyCircularProgressIndicator
import com.ondrejkomarek.composetest.utility.Failure
import com.ondrejkomarek.composetest.utility.ScreenState
import com.ondrejkomarek.composetest.utility.fold
import com.ondrejkomarek.composetest.utility.toPx
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.rememberDrawablePainter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoilApi
@Composable // for better reusability
fun PopularMovies(
	viewModel: PopularMoviesViewModel,
	darkTheme: Boolean,
	onPopularMovieClick: (Movie) -> Unit
) {
	val viewState = viewModel.state.collectAsState().value
	val moviesActivity = LocalContext.current as MoviesActivity

	//val isSystemDark = isSystemInDarkTheme()
	var isSearch: Boolean by remember { mutableStateOf(false) }
	var searchText by rememberSaveable { mutableStateOf("") }
	val focusRequester = remember { FocusRequester() }

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					if(isSearch) {
						TextField(
							value = searchText,
							onValueChange = {
								searchText = it
							},
							modifier = Modifier
								.focusRequester(focusRequester),
							singleLine = true,
							colors = TextFieldDefaults.textFieldColors(
								cursorColor = Color.White,
								backgroundColor = MaterialTheme.colors.primaryVariant,
								focusedIndicatorColor = MaterialTheme.colors.primaryVariant,
								focusedLabelColor = MaterialTheme.colors.primaryVariant
							),
							placeholder = {
								Text(
									text = "Search movies",
									color = Color.White.copy(alpha = 0.5f)
								)
							}
						)
					} else {
						Text(text = "Popular movies")
					}
				},
				actions = {
					if(isSearch.not()) {
						IconButton(onClick = LocalThemeToggle.current) {
							Icon(
								if(darkTheme) Icons.Filled.WbSunny else Icons.Filled.Nightlight,
								contentDescription = null
							)
						}
					}

					if(isSearch.not()) {
						IconButton(onClick = {
							isSearch = true
							val handler =
								Handler() // TODO without this delay it crashes, because FocusRequester is not initialized
							handler.postDelayed({
								if(isSearch) {
									focusRequester.requestFocus()
								}
							}, 200)

						}) {
							Icon(Icons.Filled.Search, contentDescription = null)
						}
					} else {
						IconButton(onClick = {
							isSearch = false
							searchText = ""
						}) {
							Icon(Icons.Filled.Close, contentDescription = null)
						}
					}
				},
				backgroundColor = MaterialTheme.colors.primaryVariant // TODO theme?
			)
		}
	) { innerPadding ->
		when(viewState.state) {
			ScreenState.CONTENT -> PopularMoviesContent(
				viewState,
				searchText,
				onPopularMovieClick,
				//Modifier.padding(innerPadding)
			)
			ScreenState.PROGRESS -> MyCircularProgressIndicator()
			ScreenState.EMPTY -> EmptyState()
		}
	}
}

@ExperimentalCoilApi
@Composable // for better reusability
fun PopularMoviesContent(
	viewState: PopularMoviesListState,
	searchText: String,
	onPopularMovieClick: (Movie) -> Unit,
	modifier: Modifier = Modifier
) {
	val scrollState = rememberLazyListState()

	Column(modifier) {
		if(viewState.movies.isNotEmpty()) {
			LazyColumn(state = scrollState) {
				items(items = viewState.movies) { movieItem ->
					if((searchText.isNotBlank() && movieItem.title.contains(
							searchText,
							ignoreCase = true
						)) || searchText.isBlank()
					) {
						MovieListCard(movieItem, onPopularMovieClick)
					}
				}
			}
		}

		if(viewState.movies.map { it.title }.filter { it.contains(searchText, ignoreCase = true) }
				.isEmpty()) {
			EmptyState()
		}
	}
}

@ExperimentalCoilApi
@Composable // for better reusability
fun MovieListCard(movieItem: Movie, onMovieClick: (Movie) -> Unit, modifier: Modifier = Modifier) {
	/*val resources = LocalContext.current.resources
	val bitmap = remember {
		val option = BitmapFactory.Options()
		option.inPreferredConfig = Bitmap.Config.ARGB_8888
		BitmapFactory.decodeResource(
			resources,
			R.drawable.trollface,
			option
		).asImageBitmap()
	}*/


	Card(
		backgroundColor = MaterialTheme.colors.surface,
		modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
	) {
		Row(
			modifier = modifier
				.clickable(onClick = { onMovieClick(movieItem) })
				.padding(16.dp)
				.fillMaxWidth(1f)
		) {
			Box(
				modifier = Modifier
					.height(120.dp)
					.wrapContentWidth(Start)
				//color = MaterialTheme.colors.onSurface,
				//shape = RoundedCornerShape(12.dp)
			) {
				/*Image(
					/*painter = rememberImagePainter(
						data = movieItem.posterUrl,
						builder = {
							ImageRequest.Builder(LocalContext.current)
								.transformations(RoundedCornersTransformation(12f))
						}
					),*/
					//bitmap = bitmap,
					painter = painterResource(id = R.drawable.trollface),
					contentScale = ContentScale.Fit,
					contentDescription = "Movie poster",
					modifier = Modifier
						.sizeIn(minHeight = 120.dp)
						.fillMaxHeight(),
				)*/

				GlideImage(
					imageModel = movieItem.posterUrl,
					// Crop, Fit, Inside, FillHeight, FillWidth, None
					contentScale = ContentScale.Fit,
					alignment = CenterStart,
					// shows an image with a circular revealed animation.
					circularReveal = CircularReveal(duration = 500),
					previewPlaceholder = R.drawable.trollface,  // does not work with success
					requestOptions = {
						RequestOptions()
							.diskCacheStrategy(DiskCacheStrategy.ALL)
							.transform(RoundedCorners(48.dp.toPx.toInt()))
					},
					modifier = Modifier
						//.fillMaxHeight()
						//.wrapContentHeight(Alignment.Top, false)//Alignment.Top, true)
						//.defaultMinSize(Dp.Hairline, 1.dp)
						.height(120.dp)
						//.width(90.dp)
						.wrapContentWidth(Start)
						.align(CenterStart)
						//.background(MaterialTheme.colors.primaryVariant)
					,
					// TODO adding success composable which actually draws drawable works - it shows properly scaled image, but circular reveal ofc does not work. Commenting this out will enable it, but screws up width scaling.
					success = {
						Image(
							painter = rememberDrawablePainter(it.drawable),
							contentScale = ContentScale.Fit,
							contentDescription = "Movie poster",
							modifier = Modifier
								.height(120.dp)
								.wrapContentWidth(Start)
						)
					}
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

	private suspend fun loadMovies() =
		movieRepository.getMovies().fold(::handleFailure, ::handleMovieList)

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


