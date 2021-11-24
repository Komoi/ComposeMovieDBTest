package com.ondrejkomarek.composetest.model

data class MovieDetail(
	val id: Int,
	val title: String,
	val overview: String?,
	val releaseDate: String,
	val posterUrl: String,
	val videoId: String?,
	val actors: List<Actor>
)