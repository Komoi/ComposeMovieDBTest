package com.ondrejkomarek.composetest.mapper

import com.ondrejkomarek.composetest.entity.ConfigurationEntity
import com.ondrejkomarek.composetest.entity.MovieEntity
import com.ondrejkomarek.composetest.model.Movie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieMapper @Inject constructor() : Mapper<Movie, Pair<ConfigurationEntity, MovieEntity>> {

	override fun mapToDomain(entity: Pair<ConfigurationEntity, MovieEntity>): Movie {
		val configurationEntity = entity.first
		val movieEntity = entity.second

		return Movie(
			movieEntity.id,
			movieEntity.title,
			movieEntity.overview,
			movieEntity.releaseDate,
			configurationEntity.images.secureBaseUrl + configurationEntity.images.posterSizes[5] + movieEntity.posterPath,
			null
		)
	}

	override fun mapToEntity(domain: Movie) = throw UnsupportedMappingException()
}