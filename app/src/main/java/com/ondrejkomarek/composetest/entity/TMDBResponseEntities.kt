package com.ondrejkomarek.composetest.entity

import com.squareup.moshi.Json

data class PopularMoviesEntity(
	@Json(name = "results")
	val results: List<MovieEntity>,
	@Json(name = "page")
	val page: Int,
	@Json(name = "total_pages")
	val totalPages: Int,
	@Json(name = "total_results")
	val totalResults: Int
)

data class MovieEntity(
	@Json(name = "id")
	val id: Int,
	@Json(name = "title")
	val title: String,
	@Json(name = "overview")
	val overview: String?,
	@Json(name = "release_date")
	val releaseDate: String,
	@Json(name = "poster_path")
	val posterPath: String
)