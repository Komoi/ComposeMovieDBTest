package com.ondrejkomarek.composetest.network

import com.ondrejkomarek.composetest.entity.ConfigurationEntity
import com.ondrejkomarek.composetest.entity.MovieEntity
import com.ondrejkomarek.composetest.entity.VideoDetailArbitraryEntity
import com.ondrejkomarek.composetest.entity.VideosEntity
import com.ondrejkomarek.composetest.mapper.MovieDetailMapper
import com.ondrejkomarek.composetest.mapper.MovieMapper
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.model.MovieDetail
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
	private val movieMapper: MovieMapper,
	private val movieDetailMapper: MovieDetailMapper
) {

	suspend fun getMovies(): Either<Failure, List<Movie>> {
		return when(networkHandler.isNetworkAvailable()) {
			true -> try {
				val configuration = api.fetchConfiguration()
				val movies = api.getPopularMovies().results

				right(movies.map { movieMapper.mapToDomain(Pair(configuration, it)) })
			} catch(exception: Throwable) {
				// TODO add error handler
				left(Failure.ServerError)
			}
			false -> left(Failure.NetworkConnection)
		}
	}

	suspend fun getMovieDetail(movieId: Int): Either<Failure, MovieDetail> {
		return when(networkHandler.isNetworkAvailable()) {
			true -> try {
				val configuration = api.fetchConfiguration()
				val movie = api.getMovieDetail(movieId)
				val videos = api.getVideos(movieId)
				val cast = api.getCredits(movieId)

				right(movieDetailMapper.mapToDomain(VideoDetailArbitraryEntity(configuration, movie, videos, cast)))
			} catch(exception: Throwable) {
				// TODO add error handler
				left(Failure.ServerError)
			}
			false -> left(Failure.NetworkConnection)
		}
	}

}