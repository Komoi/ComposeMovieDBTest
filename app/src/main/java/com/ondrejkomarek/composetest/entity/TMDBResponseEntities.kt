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

data class VideosEntity(
	val id: Int?,
	val results: List<VideoEntity>?
)

data class VideoEntity(
	val iso_639_1: String?,
	val iso_3166_1: String?,
	val name: String?,
	val key: String?,
	val site: String?,
	val size: Int?,
	val type: String?,
	val official: Boolean?,
	@Json(name = "published_at")
	val publishedAt: String?,
	val id: String?
)