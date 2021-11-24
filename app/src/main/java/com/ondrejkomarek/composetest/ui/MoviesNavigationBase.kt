package com.ondrejkomarek.composetest.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ondrejkomarek.composetest.navigation.AppNavigationScreens
import com.ondrejkomarek.composetest.ui.movie_detail.MovieDetail
import com.ondrejkomarek.composetest.ui.popular_movies.PopularMovies
import com.ondrejkomarek.composetest.ui.popular_movies.PopularMoviesViewModel
import com.ondrejkomarek.composetest.ui.theme.ComposeTestTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.ui.movie_detail.MovieDetailViewModel

const val movieIdArg = "movie_id"
const val movieNameArg = "movie_name"

@AndroidEntryPoint
class MoviesActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContent {
			ComposeTestTheme {
				// A surface container using the 'background' color from the theme
				MoviesApp()
			}
		}
	}
}

@Composable
fun MoviesApp() {
	val allScreens = AppNavigationScreens.values().toList()
	/*
	Need to import manually
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
	 */
	var currentScreen by rememberSaveable { mutableStateOf(AppNavigationScreens.PopularMovies) }
	val navController = rememberNavController()
	Movies(navController) {
		movie ->
		navController.navigate("${AppNavigationScreens.MovieDetail.name}/${movie.id}/${movie.title}")

	}
}

@Composable
fun Movies(
	navController: NavHostController,
	onPopularMovieClick: (Movie) -> Unit
) {

	MoviesContent(
		navController,
		onPopularMovieClick
	)
}

@Composable
fun MoviesContent(navController: NavHostController, onPopularMovieClick: (Movie) -> Unit) {
		NavHost(
			navController = navController,
			startDestination = AppNavigationScreens.PopularMovies.name,
			//modifier = Modifier.padding(innerPadding)
		) {
			composable(AppNavigationScreens.PopularMovies.name) {
				val viewModel = hiltViewModel<PopularMoviesViewModel>()
				PopularMovies(viewModel, onPopularMovieClick)
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