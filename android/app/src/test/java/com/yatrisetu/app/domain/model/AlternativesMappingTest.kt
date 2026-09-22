package com.yatrisetu.app.domain.model

import com.yatrisetu.app.data.mapper.toDomain
import com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceResponseDto
import com.yatrisetu.app.data.remote.dto.AlternativeRecommendationDto
import com.yatrisetu.app.data.remote.dto.AlternativeWeatherDto
import com.yatrisetu.app.data.remote.dto.AlternativesResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlternativesMappingTest {

    @Test
    fun testAlternativesResponseDtoToDomainMapping() {
        val weatherDto = AlternativeWeatherDto(
            destinationId = "kalimpong",
            temperature = 16.5f,
            tempMinC = 12f,
            tempMaxC = 20f,
            condition = "Clear Sky",
            humidity = 55,
            provenanceLabel = "REAL — SATELLITE",
            providerMode = "REAL",
            cacheStatus = "LIVE"
        )

        val recDto1 = AlternativeRecommendationDto(
            id = "kalimpong",
            name = "Kalimpong",
            tagline = "Ridge flower haven",
            state = "West Bengal",
            heroImage = "https://images.unsplash.com/photo-1",
            crowdScore = 35,
            crowdLevel = "LOW",
            similarityScore = 88,
            similarityProvenance = "DEMO_CALIBRATION_BENCHMARK",
            originalCrowdScore = 88,
            alternativeCrowdScore = 35,
            crowdReductionPercent = 60,
            distanceKm = 52.4f,
            estimatedCostPerDay = 2500,
            costDifferencePercent = 35,
            reasonsToRecommend = listOf("High similarity with serene orchids", "Low traffic corridor"),
            sharedHighlights = listOf("Monasteries", "Ridge views"),
            matchingAttributes = listOf("Scenic", "Heritage"),
            keyExperience = "Orchid nursery walks",
            ecoTag = "COMMUNITY_MANAGED",
            capacityStatus = "HEALTHY",
            availableCapacity = 140,
            accessStatus = "OPEN",
            weather = weatherDto,
            reasons = listOf("Lower crowd pressure with similar mountain experience."),
            provenance = "REAL — YATRI SETU NETWORK"
        )

        val recDto2 = AlternativeRecommendationDto(
            id = "rishop",
            name = "Rishop",
            crowdScore = 15,
            crowdLevel = "LOW",
            similarityScore = 82,
            crowdReductionPercent = 83,
            distanceKm = 64f,
            estimatedCostPerDay = 2000,
            costDifferencePercent = 50,
            reasons = listOf("360 degree Kanchenjunga view with zero vehicular congestion.")
        )

        val responseDto = AlternativesResponseDto(
            originDestinationId = "darjeeling",
            originDestinationName = "Darjeeling",
            originCrowdScore = 88,
            originCrowdLevel = "VERY HIGH",
            alternatives = listOf(recDto1, recDto2)
        )

        val domain = responseDto.toDomain(isLive = true, cachedAt = 5000L)

        // Verify origin mapping
        assertEquals("darjeeling", domain.originDestinationId)
        assertEquals("Darjeeling", domain.originDestinationName)
        assertEquals(88, domain.originCrowdScore)
        assertEquals(CrowdLevel.VERY_HIGH, domain.originCrowdLevel)
        assertTrue(domain.isLive)
        assertEquals(5000L, domain.cachedAt)
        assertEquals("LIVE · Server Recommendations", domain.freshnessLabel)

        // Verify alternatives list size & order preserved (backend authority)
        assertEquals(2, domain.alternatives.size)
        val alt1 = domain.alternatives[0]
        val alt2 = domain.alternatives[1]

        // Verify alt 1 fields match backend EXACTLY (no client-side recomputation)
        assertEquals("kalimpong", alt1.id)
        assertEquals("Kalimpong", alt1.name)
        assertEquals(35, alt1.crowdScore)
        assertEquals(CrowdLevel.LOW, alt1.crowdLevel)
        assertEquals(88, alt1.similarityScore)
        assertEquals(60, alt1.crowdReductionPercent)
        assertEquals(52.4f, alt1.distanceKm, 0.01f)
        assertEquals(2500, alt1.estimatedCostPerDay)
        assertEquals(35, alt1.costDifferencePercent)
        assertEquals("HEALTHY", alt1.capacityStatus)
        assertEquals("OPEN", alt1.accessStatus)

        // Verify formatted presentation helpers
        assertEquals("60% less crowded", alt1.crowdReductionLabel)
        assertEquals("88% similar", alt1.similarityLabel)
        assertEquals("52 km away", alt1.distanceLabel)
        assertEquals("₹2500 / day", alt1.dailyCostLabel)
        assertEquals("35% lower expense", alt1.costSavingsLabel)
        assertEquals("Lower crowd pressure with similar mountain experience.", alt1.primaryReason)

        val weather = alt1.weather
        assertNotNull(weather)
        assertEquals(16.5f, weather?.temperature ?: 0f, 0.01f)
        assertEquals("Clear Sky", weather?.condition)

        // Verify alt 2 fallbacks
        assertEquals("rishop", alt2.id)
        assertEquals("Rishop", alt2.name)
        assertNull(alt2.weather)
        assertEquals("83% less crowded", alt2.crowdReductionLabel)
        assertEquals("82% similar", alt2.similarityLabel)
    }

    @Test
    fun testAlternativeAcceptanceMapping() {
        val dto = AlternativeAcceptanceResponseDto(
            status = "recorded",
            eventId = "evt-12345",
            originalDestinationId = "darjeeling",
            alternativeDestinationId = "kalimpong"
        )
        val domain = dto.toDomain()
        assertEquals("recorded", domain.status)
        assertEquals("evt-12345", domain.eventId)
        assertEquals("darjeeling", domain.originalDestinationId)
        assertEquals("kalimpong", domain.alternativeDestinationId)
    }
}
