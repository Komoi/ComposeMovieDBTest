package com.ondrejkomarek.composetest.network

import com.ondrejkomarek.composetest.entity.ConfigurationEntity
import com.ondrejkomarek.composetest.entity.MovieEntity
import com.ondrejkomarek.composetest.entity.PopularMoviesEntity
import com.ondrejkomarek.composetest.entity.VideosEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

	@GET("configuration")
	suspend fun fetchConfiguration(): ConfigurationEntity

	@GET("movie/popular")
	suspend fun getPopularMovies(@Query("page") page: Int = 1): PopularMoviesEntity

	@GET("movie/{movieId}")
	suspend fun getMovieDetail(@Path("movieId") movieId: Int): MovieEntity

	@GET("movie/{movieId}/videos")
	suspend fun getVideos(@Path("movieId") movieId: Int): VideosEntity
}
