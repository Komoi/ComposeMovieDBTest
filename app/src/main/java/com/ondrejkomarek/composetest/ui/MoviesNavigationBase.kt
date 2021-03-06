package com.ondrejkomarek.composetest.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.navigation.AppNavigationScreens
import com.ondrejkomarek.composetest.ui.movie_detail.MovieDetail
import com.ondrejkomarek.composetest.ui.movie_detail.MovieDetailViewModel
import com.ondrejkomarek.composetest.ui.popular_movies.PopularMovies
import com.ondrejkomarek.composetest.ui.popular_movies.PopularMoviesViewModel
import com.ondrejkomarek.composetest.ui.theme.ComposeTestTheme
import com.ondrejkomarek.composetest.ui.universal.CircularReveal
import com.ondrejkomarek.composetest.ui.universal.LocalThemeToggle
import dagger.hilt.android.AndroidEntryPoint

const val movieIdArg = "movie_id"
const val movieNameArg = "movie_name"

@AndroidEntryPoint
class MoviesActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContent {

			val isSystemDark = isSystemInDarkTheme()
			var darkTheme: Boolean by remember { mutableStateOf(isSystemDark) }

			CompositionLocalProvider(LocalThemeToggle provides { darkTheme = !darkTheme }) {
				CircularReveal(darkTheme, animationSpec = tween(1000)) { theme ->
					ComposeTestTheme(theme) {
						MoviesApp(darkTheme)
					}
				}
			}
		}
	}
}

@Composable
fun MoviesApp(darkTheme: Boolean) {
	val navController = rememberNavController()
	Movies(navController, darkTheme) { movie ->
		navController.navigate("${AppNavigationScreens.MovieDetail.name}/${movie.id}/${movie.title}")

	}
}

@Composable
fun Movies(
	navController: NavHostController,
	darkTheme: Boolean,
	onPopularMovieClick: (Movie) -> Unit
) {
	MoviesContent(
		navController,
		darkTheme,
		onPopularMovieClick
	)
}

@Composable
fun MoviesContent(
	navController: NavHostController,
	darkTheme: Boolean,
	onPopularMovieClick: (Movie) -> Unit
) {
	NavHost(
		navController = navController,
		startDestination = AppNavigationScreens.PopularMovies.name,
	) {
		composable(AppNavigationScreens.PopularMovies.name) {
			val viewModel = hiltViewModel<PopularMoviesViewModel>()
			PopularMovies(viewModel, darkTheme, onPopularMovieClick)
		}


		composable(route = "${AppNavigationScreens.MovieDetail.name}/{$movieIdArg}/{$movieNameArg}",
			arguments = listOf(
				navArgument(movieIdArg) {
					type = NavType.IntType
				},
				navArgument(movieNameArg) {
					type = NavType.StringType
				}
			)) {
			val viewModel = hiltViewModel<MovieDetailViewModel>()
			MovieDetail(viewModel)
		}
	}
}