package com.yatrisetu.app.data.mapper

import com.yatrisetu.app.data.local.entity.CachedCrowdEntity
import com.yatrisetu.app.data.local.entity.DestinationEntity
import com.yatrisetu.app.data.remote.dto.AttractionDto
import com.yatrisetu.app.data.remote.dto.CoordinatesDto
import com.yatrisetu.app.data.remote.dto.CrowdFactorDto
import com.yatrisetu.app.data.remote.dto.CrowdResponseDto
import com.yatrisetu.app.data.remote.dto.DestinationAttributesDto
import com.yatrisetu.app.data.remote.dto.DestinationDetailDto
import com.yatrisetu.app.data.remote.dto.DestinationSummaryDto
import com.yatrisetu.app.domain.model.Attraction
import com.yatrisetu.app.domain.model.CrowdFactor
import com.yatrisetu.app.domain.model.CrowdInfo
import com.yatrisetu.app.domain.model.CrowdLevel
import com.yatrisetu.app.domain.model.Destination
import com.yatrisetu.app.domain.model.DestinationAttributes
import com.yatrisetu.app.domain.model.DestinationSummary
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun DestinationSummaryDto.toDomain(isLive: Boolean = true, cachedAt: Long? = null): DestinationSummary {
    val level = if (crowdLevel.isNotBlank()) {
        CrowdLevel.fromString(crowdLevel)
    } else {
        CrowdLevel.fromScore(crowdScore)
    }

    return DestinationSummary(
        id = id,
        name = name,
        tagline = tagline,
        region = region,
        state = state,
        heroImage = heroImage,
        crowdScore = crowdScore.coerceIn(0, 100),
        crowdLevel = level,
        avgCostPerDayInr = avgCostPerDayInr,
        tags = tags,
        isLive = isLive,
        cachedAt = cachedAt
    )
}

fun DestinationDetailDto.toDomain(isLive: Boolean = true, cachedAt: Long? = null): Destination {
    val defaultAttributes = attributes ?: DestinationAttributesDto()
    return Destination(
        id = id,
        name = name,
        tagline = tagline,
        region = region,
        state = state,
        description = description,
        latitude = coordinates?.lat ?: 0.0,
        longitude = coordinates?.lng ?: 0.0,
        heroImage = heroImage,
        galleryImages = galleryImages,
        attributes = DestinationAttributes(
            nature = defaultAttributes.nature,
            climate = defaultAttributes.climate,
            activities = defaultAttributes.activities,
            culture = defaultAttributes.culture,
            budgetLevel = defaultAttributes.budgetLevel,
            avgCostPerDayInr = defaultAttributes.avgCostPerDayInr,
            accessibility = defaultAttributes.accessibility,
            altitudeFt = defaultAttributes.altitudeFt
        ),
        highlights = highlights,
        attractions = attractions.map { it.toDomain() },
        baseCrowdScore = baseCrowdScore,
        recommendedDurationDays = recommendedDurationDays,
        tags = tags,
        isLive = isLive,
        cachedAt = cachedAt
    )
}

fun AttractionDto.toDomain(): Attraction {
    return Attraction(
        id = id,
        name = name,
        category = category,
        description = description,
        crowdDensity = crowdDensity,
        visitDurationHrs = visitDurationHrs,
        bestTime = bestTime,
        imageUrl = imageUrl
    )
}

fun CrowdFactorDto.toDomain(): CrowdFactor {
    return CrowdFactor(
        name = name,
        key = key,
        rawValue = rawValue,
        weightPercentage = weightPercentage,
        weightedContribution = weightedContribution,
        description = description
    )
}

fun CrowdResponseDto.toDomain(isLive: Boolean = true, cachedAt: Long? = null): CrowdInfo {
    val level = if (crowdLevel.isNotBlank()) {
        CrowdLevel.fromString(crowdLevel)
    } else {
        CrowdLevel.fromScore(crowdScore)
    }

    return CrowdInfo(
        destinationId = destinationId,
        destinationName = destinationName,
        crowdScore = crowdScore.coerceIn(0, 100),
        crowdLevel = level,
        colorCode = colorCode,
        summary = summary,
        whyCrowded = whyCrowded,
        bottlenecks = bottlenecks,
        factors = factors.map { it.toDomain() },
        liveTrafficStatus = liveTrafficStatus,
        hotelOccupancyRate = hotelOccupancyRate,
        lastUpdated = lastUpdated,
        peakVisitingHours = peakVisitingHours,
        bestTimeToVisitToday = bestTimeToVisitToday,
        provenanceLabel = provenanceLabel,
        providerMode = providerMode,
        dataQuality = dataQuality,
        pressureScore = pressureScore,
        pressureLevel = pressureLevel,
        confidencePercent = confidencePercent,
        carryingCapacityPercent = carryingCapacityPercent,
        advisory = advisory,
        recommendedAction = recommendedAction,
        isLive = isLive,
        cachedAt = cachedAt
    )
}

// Room entity mappers
fun DestinationSummary.toEntity(cachedAt: Long = System.currentTimeMillis()): DestinationEntity {
    return DestinationEntity(
        id = id,
        name = name,
        tagline = tagline,
        region = region,
        state = state,
        heroImage = heroImage,
        crowdScore = crowdScore,
        crowdLevel = crowdLevel.label,
        avgCostPerDayInr = avgCostPerDayInr,
        tags = tags.joinToString(","),
        cachedAt = cachedAt
    )
}

fun DestinationEntity.toDomain(): DestinationSummary {
    val level = CrowdLevel.fromString(crowdLevel)
    return DestinationSummary(
        id = id,
        name = name,
        tagline = tagline,
        region = region,
        state = state,
        heroImage = heroImage,
        crowdScore = crowdScore.coerceIn(0, 100),
        crowdLevel = level,
        avgCostPerDayInr = avgCostPerDayInr,
        tags = if (tags.isNotBlank()) tags.split(",") else emptyList(),
        isLive = false,
        cachedAt = cachedAt
    )
}

fun CrowdInfo.toEntity(cachedAt: Long = System.currentTimeMillis()): CachedCrowdEntity {
    val factorsJson = try {
        val dtos = factors.map {
            CrowdFactorDto(
                name = it.name,
                key = it.key,
                rawValue = it.rawValue,
                weightPercentage = it.weightPercentage,
                weightedContribution = it.weightedContribution,
                description = it.description
            )
        }
        json.encodeToString(dtos)
    } catch (e: Exception) {
        "[]"
    }

    return CachedCrowdEntity(
        destinationId = destinationId,
        destinationName = destinationName,
        crowdScore = crowdScore,
        crowdLevel = crowdLevel.label,
        colorCode = colorCode,
        summary = summary,
        whyCrowded = whyCrowded.joinToString("\n"),
        bottlenecks = bottlenecks.joinToString("\n"),
        factorsJson = factorsJson,
        liveTrafficStatus = liveTrafficStatus,
        hotelOccupancyRate = hotelOccupancyRate,
        lastUpdated = lastUpdated,
        peakVisitingHours = peakVisitingHours,
        bestTimeToVisitToday = bestTimeToVisitToday,
        provenanceLabel = provenanceLabel,
        cachedAt = cachedAt
    )
}

fun CachedCrowdEntity.toDomain(): CrowdInfo {
    val factorList: List<CrowdFactor> = try {
        val dtos: List<CrowdFactorDto> = json.decodeFromString(factorsJson)
        dtos.map { it.toDomain() }
    } catch (e: Exception) {
        emptyList()
    }

    return CrowdInfo(
        destinationId = destinationId,
        destinationName = destinationName,
        crowdScore = crowdScore.coerceIn(0, 100),
        crowdLevel = CrowdLevel.fromString(crowdLevel),
        colorCode = colorCode,
        summary = summary,
        whyCrowded = if (whyCrowded.isNotBlank()) whyCrowded.split("\n") else emptyList(),
        bottlenecks = if (bottlenecks.isNotBlank()) bottlenecks.split("\n") else emptyList(),
        factors = factorList,
        liveTrafficStatus = liveTrafficStatus,
        hotelOccupancyRate = hotelOccupancyRate,
        lastUpdated = lastUpdated,
        peakVisitingHours = peakVisitingHours,
        bestTimeToVisitToday = bestTimeToVisitToday,
        provenanceLabel = provenanceLabel ?: "CACHED LOCAL SNAPSHOT",
        isLive = false,
        cachedAt = cachedAt
    )
}
