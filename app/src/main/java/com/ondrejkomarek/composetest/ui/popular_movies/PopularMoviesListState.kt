package com.ondrejkomarek.composetest.ui.popular_movies

import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.utility.ScreenState

data class PopularMoviesListState(
	val state: ScreenState = ScreenState.PROGRESS,
	val movies: List<Movie> = emptyList()
)