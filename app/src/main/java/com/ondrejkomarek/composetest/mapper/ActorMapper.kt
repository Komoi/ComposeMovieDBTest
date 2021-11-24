package com.ondrejkomarek.composetest.mapper

import com.ondrejkomarek.composetest.entity.CastEntity
import com.ondrejkomarek.composetest.entity.ConfigurationEntity
import com.ondrejkomarek.composetest.entity.MovieEntity
import com.ondrejkomarek.composetest.model.Actor
import com.ondrejkomarek.composetest.model.Movie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActorMapper @Inject constructor() : Mapper<Actor, Pair<ConfigurationEntity, CastEntity>> {

	override fun mapToDomain(entity: Pair<ConfigurationEntity, CastEntity>): Actor {
		val configurationEntity = entity.first
		val castEntity = entity.second

		return Actor(
			castEntity.castId,
			castEntity.name,
			castEntity.character,
			configurationEntity.images.secureBaseUrl + configurationEntity.images.profileSizes[3] + castEntity.profilePath
		)
	}

	override fun mapToEntity(domain: Actor) = throw UnsupportedMappingException()
}