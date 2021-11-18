package com.ondrejkomarek.composetest.network

import com.ondrejkomarek.composetest.mapper.MovieMapper
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.utility.Either
import com.ondrejkomarek.composetest.utility.Failure
import com.ondrejkomarek.composetest.utility.left
import com.ondrejkomarek.composetest.utility.right
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
	private val networkHandler: NetworkHandler,
	private val api: MovieApi,
	private val movieMapper: MovieMapper
) {

	suspend fun getMovies(): Either<Failure, List<Movie>> {
		return when(networkHandler.isNetworkAvailable()) {
			true -> try {
				val configuration = api.fetchConfiguration()
				val movies = api.fetchPopularMovies().results

				right(movies.map { movieMapper.mapToDomain(Pair(configuration, it)) })
			} catch(exception: Throwable) {
				// TODO add error handler
				left(Failure.ServerError)
			}
			false -> left(Failure.NetworkConnection)
		}
	}
}