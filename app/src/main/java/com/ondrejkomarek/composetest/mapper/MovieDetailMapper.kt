package com.ondrejkomarek.composetest.mapper

import com.ondrejkomarek.composetest.entity.ConfigurationEntity
import com.ondrejkomarek.composetest.entity.MovieEntity
import com.ondrejkomarek.composetest.entity.VideoDetailArbitraryEntity
import com.ondrejkomarek.composetest.entity.VideosEntity
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.model.MovieDetail
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDetailMapper @Inject constructor(
	val actorMapper: ActorMapper
) : Mapper<MovieDetail, VideoDetailArbitraryEntity> {

	override fun mapToDomain(entity: VideoDetailArbitraryEntity): MovieDetail {

		val videoId = entity.videosEntity.results
			?.filter { it.site == "YouTube" }
			?.filter { it.type == "Trailer" }
			?.map { it.key }
			?.firstOrNull()

		return MovieDetail(
			entity.movieEntity.id,
			entity.movieEntity.title,
			entity.movieEntity.overview,
			entity.movieEntity.releaseDate,
			entity.configurationEntity.images.secureBaseUrl + entity.configurationEntity.images.posterSizes[5] + entity.movieEntity.posterPath,
			videoId,
			entity.creditsEntity.cast.map { actorMapper.mapToDomain(Pair(entity.configurationEntity, it)) }
		)
	}

	override fun mapToEntity(domain: MovieDetail) = throw UnsupportedMappingException()

}