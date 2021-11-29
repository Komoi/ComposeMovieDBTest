package com.ondrejkomarek.composetest.ui.universal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil.annotation.ExperimentalCoilApi
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.ui.popular_movies.MovieListCard
import com.ondrejkomarek.composetest.ui.popular_movies.PopularMoviesListState

@Composable
fun MyCircularProgressIndicator(modifier: Modifier = Modifier) {
	Column(modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally) {
		CircularProgressIndicator()
	}
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
	Column(modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally) {
		CircularProgressIndicator()
	}
}