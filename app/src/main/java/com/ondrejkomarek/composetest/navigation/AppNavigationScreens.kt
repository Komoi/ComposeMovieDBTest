package com.ondrejkomarek.composetest.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppNavigationScreens(
	val icon: ImageVector,
) {
	PopularMovies(
		icon = Icons.Filled.Favorite,
	),
	MovieDetail(
		icon = Icons.Filled.Details,
	);

	companion object {
		fun fromRoute(route: String?): AppNavigationScreens =
			when (route?.substringBefore("/")) {
				PopularMovies.name -> PopularMovies
				MovieDetail.name -> MovieDetail
				null -> PopularMovies
				else -> throw IllegalArgumentException("Route $route is not recognized.")
			}
	}
}