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
import com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceResponseDto
import com.yatrisetu.app.data.remote.dto.AlternativeRecommendationDto
import com.yatrisetu.app.data.remote.dto.AlternativeWeatherDto
import com.yatrisetu.app.data.remote.dto.AlternativesResponseDto
import com.yatrisetu.app.data.remote.dto.ActivitySlotDto
import com.yatrisetu.app.data.remote.dto.ItineraryDayDto
import com.yatrisetu.app.data.remote.dto.ItineraryResponseDto
import com.yatrisetu.app.data.remote.dto.ItineraryRequestDto
import com.yatrisetu.app.domain.model.AlternativeAcceptance
import com.yatrisetu.app.domain.model.AlternativeDestination
import com.yatrisetu.app.domain.model.AlternativeWeatherInfo
import com.yatrisetu.app.domain.model.AlternativesData
import com.yatrisetu.app.domain.model.ActivitySlot
import com.yatrisetu.app.domain.model.ItineraryDay
import com.yatrisetu.app.domain.model.Itinerary
import com.yatrisetu.app.domain.model.ItineraryRequest
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

fun AlternativeWeatherDto.toDomain(): AlternativeWeatherInfo {
    return AlternativeWeatherInfo(
        destinationId = destinationId,
        temperature = temperature,
        tempMinC = tempMinC,
        tempMaxC = tempMaxC,
        condition = condition,
        humidity = humidity,
        precipitationChance = precipitationChance,
        provenanceLabel = provenanceLabel,
        providerMode = providerMode,
        cacheStatus = cacheStatus,
        observedAt = observedAt,
        temperatureRange = temperatureRange
    )
}

fun AlternativeRecommendationDto.toDomain(): AlternativeDestination {
    val level = if (crowdLevel.isNotBlank()) {
        CrowdLevel.fromString(crowdLevel)
    } else {
        CrowdLevel.fromScore(crowdScore)
    }

    return AlternativeDestination(
        id = id,
        name = name,
        tagline = tagline,
        state = state,
        heroImage = heroImage,
        crowdScore = crowdScore.coerceIn(0, 100),
        crowdLevel = level,
        similarityScore = similarityScore.coerceIn(0, 100),
        similarityProvenance = similarityProvenance,
        originalCrowdScore = originalCrowdScore,
        alternativeCrowdScore = alternativeCrowdScore,
        crowdReductionPercent = crowdReductionPercent,
        distanceKm = distanceKm,
        geographicDistanceKm = geographicDistanceKm,
        roadDistanceKm = roadDistanceKm,
        distanceProvenance = distanceProvenance,
        estimatedCostPerDay = estimatedCostPerDay,
        costDifferencePercent = costDifferencePercent,
        reasonsToRecommend = reasonsToRecommend,
        sharedHighlights = sharedHighlights,
        matchingAttributes = matchingAttributes,
        keyExperience = keyExperience,
        ecoTag = ecoTag,
        destinationId = destinationId,
        currentPressure = currentPressure,
        expectedPressure = expectedPressure,
        capacityStatus = capacityStatus,
        availableCapacity = availableCapacity,
        accessStatus = accessStatus,
        weather = weather?.toDomain(),
        weatherSummary = weatherSummary,
        trafficSummary = trafficSummary,
        homestayAvailability = homestayAvailability,
        reasons = reasons,
        provenance = provenance,
        lastUpdated = lastUpdated
    )
}

fun AlternativesResponseDto.toDomain(
    isLive: Boolean = true,
    cachedAt: Long? = null
): AlternativesData {
    val originLevel = if (originCrowdLevel.isNotBlank()) {
        CrowdLevel.fromString(originCrowdLevel)
    } else {
        CrowdLevel.fromScore(originCrowdScore)
    }

    return AlternativesData(
        originDestinationId = originDestinationId,
        originDestinationName = originDestinationName,
        originCrowdScore = originCrowdScore.coerceIn(0, 100),
        originCrowdLevel = originLevel,
        alternatives = alternatives.map { it.toDomain() },
        isLive = isLive,
        cachedAt = cachedAt
    )
}

fun AlternativeAcceptanceResponseDto.toDomain(): AlternativeAcceptance {
    return AlternativeAcceptance(
        status = status,
        eventId = eventId,
        originalDestinationId = originalDestinationId,
        alternativeDestinationId = alternativeDestinationId
    )
}

// ==========================================
// Phase 4: Itinerary Mappers
// ==========================================

fun ActivitySlotDto.toDomain(): ActivitySlot {
    // Defensively fill fields that Groq LLM may omit, preventing null/blank
    // values from propagating into the domain and causing 422 on optimize calls.
    val safePeriod = period.ifBlank {
        // Infer period from time_slot prefix as a best-effort fallback
        when {
            timeSlot.startsWith("05:") || timeSlot.startsWith("06:") || timeSlot.startsWith("07:") ||
                timeSlot.startsWith("08:") || timeSlot.startsWith("09:") || timeSlot.startsWith("10:") ||
                timeSlot.startsWith("11:") -> "Morning"
            timeSlot.startsWith("12:") || timeSlot.startsWith("13:") || timeSlot.startsWith("14:") -> "Afternoon"
            timeSlot.startsWith("15:") || timeSlot.startsWith("16:") || timeSlot.startsWith("17:") -> "Afternoon"
            else -> "Evening"
        }
    }
    return ActivitySlot(
        timeSlot = timeSlot,
        period = safePeriod,
        title = title,
        description = description,
        locationName = locationName,
        crowdForecast = crowdForecast.ifBlank { "Moderate" },
        costEstimateInr = costEstimateInr,
        durationHrs = if (durationHrs <= 0f) 1.5f else durationHrs,
        travelTip = travelTip,
        category = category.ifBlank { "Culture" },
        imageUrl = imageUrl,
        isWeatherAdapted = isWeatherAdapted,
        adaptationReason = adaptationReason
    )
}

fun ActivitySlot.toDto(): ActivitySlotDto {
    // Apply fallbacks for required Pydantic fields — Groq LLM may produce
    // activities missing period/crowd_forecast/duration_hrs/category.
    // Empty or zero values must be replaced with Pydantic-acceptable defaults
    // before the optimize request is sent to the backend.
    return ActivitySlotDto(
        timeSlot = timeSlot,
        period = period.ifBlank { "Morning" },
        title = title,
        description = description,
        locationName = locationName,
        crowdForecast = crowdForecast.ifBlank { "Moderate" },
        costEstimateInr = costEstimateInr,
        durationHrs = if (durationHrs <= 0f) 1.5f else durationHrs,
        travelTip = travelTip,
        category = category.ifBlank { "Culture" },
        imageUrl = imageUrl,
        isWeatherAdapted = isWeatherAdapted,
        adaptationReason = adaptationReason
    )
}

fun ItineraryDayDto.toDomain(): ItineraryDay {
    return ItineraryDay(
        dayNumber = dayNumber,
        theme = theme,
        overview = overview,
        estimatedBudgetInr = estimatedBudgetInr,
        activities = activities.map { it.toDomain() },
        transitAdvice = transitAdvice
    )
}

fun ItineraryDay.toDto(): ItineraryDayDto {
    return ItineraryDayDto(
        dayNumber = dayNumber,
        theme = theme,
        overview = overview,
        estimatedBudgetInr = estimatedBudgetInr,
        activities = activities.map { it.toDto() },
        transitAdvice = transitAdvice.ifBlank { "Local shared transit and walking recommended." }
    )
}

fun ItineraryResponseDto.toDomain(
    isLive: Boolean = true,
    cachedAt: Long? = null
): Itinerary {
    return Itinerary(
        itineraryId = itineraryId,
        destinationId = destinationId,
        destinationName = destinationName,
        durationDays = durationDays,
        travelerType = travelerType,
        pace = pace,
        interests = interests,
        totalEstimatedBudgetInr = totalEstimatedBudgetInr,
        crowdAvoidanceRating = crowdAvoidanceRating,
        localEconomicImpactTag = localEconomicImpactTag,
        days = days.map { it.toDomain() },
        aiGeneratedNote = aiGeneratedNote,
        sustainabilityScore = sustainabilityScore,
        sustainabilityClassification = sustainabilityClassification,
        weatherAdaptationNotice = weatherAdaptationNotice,
        whyThisItinerary = whyThisItinerary,
        aiProviderUsed = aiProviderUsed,
        optimizationHistory = optimizationHistory,
        isLive = isLive,
        cachedAt = cachedAt
    )
}

fun Itinerary.toResponseDto(): ItineraryResponseDto {
    return ItineraryResponseDto(
        itineraryId = itineraryId,
        destinationId = destinationId,
        destinationName = destinationName,
        durationDays = durationDays,
        travelerType = travelerType,
        pace = pace,
        interests = interests,
        totalEstimatedBudgetInr = totalEstimatedBudgetInr,
        crowdAvoidanceRating = crowdAvoidanceRating,
        localEconomicImpactTag = localEconomicImpactTag,
        days = days.map { it.toDto() },
        aiGeneratedNote = aiGeneratedNote,
        sustainabilityScore = sustainabilityScore,
        sustainabilityClassification = sustainabilityClassification,
        weatherAdaptationNotice = weatherAdaptationNotice,
        whyThisItinerary = whyThisItinerary,
        aiProviderUsed = aiProviderUsed,
        optimizationHistory = optimizationHistory
    )
}

fun ItineraryRequest.toDto(): ItineraryRequestDto {
    return ItineraryRequestDto(
        destinationId = destinationId,
        durationDays = durationDays.coerceIn(1, 5),
        travelerType = travelerType,
        pace = pace,
        interests = interests,
        budgetLevel = budgetLevel,
        startDate = startDate,
        optimizeForWeather = optimizeForWeather
    )
}


