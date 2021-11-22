package com.ondrejkomarek.composetest.ui.movie_detail

import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.utility.ScreenState

data class MovieDetailState(
	val state: ScreenState = ScreenState.PROGRESS,
	val movie: Movie? = null
)