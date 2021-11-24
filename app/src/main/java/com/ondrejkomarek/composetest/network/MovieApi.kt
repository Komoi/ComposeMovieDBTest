package com.ondrejkomarek.composetest.network

import com.ondrejkomarek.composetest.entity.*
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

	@GET("movie/{movieId}/credits")
	suspend fun getCredits(@Path("movieId") movieId: Int): CreditsEntity
}
