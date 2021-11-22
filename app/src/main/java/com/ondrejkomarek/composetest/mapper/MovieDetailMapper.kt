package com.ondrejkomarek.composetest.mapper

import com.ondrejkomarek.composetest.entity.ConfigurationEntity
import com.ondrejkomarek.composetest.entity.MovieEntity
import com.ondrejkomarek.composetest.entity.VideosEntity
import com.ondrejkomarek.composetest.model.Movie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDetailMapper @Inject constructor() : Mapper<Movie, Triple<ConfigurationEntity, MovieEntity, VideosEntity>> {

	override fun mapToDomain(entity: Triple<ConfigurationEntity, MovieEntity, VideosEntity>): Movie {
		val configurationEntity = entity.first
		val movieEntity = entity.second
		val videosEntity = entity.third

		val videoId = videosEntity.results
			?.filter { it.site == "YouTube" }
			?.filter { it.type == "Trailer" }
			?.map { it.key }
			?.firstOrNull()

		return Movie(
			movieEntity.id,
			movieEntity.title,
			movieEntity.overview,
			movieEntity.releaseDate,
			configurationEntity.images.secureBaseUrl + configurationEntity.images.posterSizes[5] + movieEntity.posterPath,
			videoId
		)
	}

	override fun mapToEntity(domain: Movie) = throw UnsupportedMappingException()

}