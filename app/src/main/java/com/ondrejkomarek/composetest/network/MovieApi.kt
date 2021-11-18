package com.ondrejkomarek.composetest.network

import com.ondrejkomarek.composetest.entity.ConfigurationEntity
import com.ondrejkomarek.composetest.entity.PopularMoviesEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

	@GET("configuration")
	suspend fun fetchConfiguration(): ConfigurationEntity

	@GET("movie/popular")
	suspend fun fetchPopularMovies(@Query("page") page: Int = 1): PopularMoviesEntity
}
