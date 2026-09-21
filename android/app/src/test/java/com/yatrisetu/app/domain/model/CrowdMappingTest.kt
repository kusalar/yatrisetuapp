package com.yatrisetu.app.domain.model

import com.yatrisetu.app.data.mapper.toDomain
import com.yatrisetu.app.data.remote.dto.CrowdFactorDto
import com.yatrisetu.app.data.remote.dto.CrowdResponseDto
import com.yatrisetu.app.data.remote.dto.DestinationSummaryDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CrowdMappingTest {

    @Test
    fun testCrowdLevelThresholds() {
        // Documented fallback thresholds:
        // < 40 = LOW
        // 40–59 = MODERATE (MEDIUM)
        // 60–79 = HIGH
        // >= 80 = CRITICAL (VERY HIGH)
        assertEquals(CrowdLevel.LOW, CrowdLevel.fromScore(0))
        assertEquals(CrowdLevel.LOW, CrowdLevel.fromScore(39))
        assertEquals(CrowdLevel.MEDIUM, CrowdLevel.fromScore(40))
        assertEquals(CrowdLevel.MEDIUM, CrowdLevel.fromScore(59))
        assertEquals(CrowdLevel.HIGH, CrowdLevel.fromScore(60))
        assertEquals(CrowdLevel.HIGH, CrowdLevel.fromScore(79))
        assertEquals(CrowdLevel.VERY_HIGH, CrowdLevel.fromScore(80))
        assertEquals(CrowdLevel.VERY_HIGH, CrowdLevel.fromScore(100))
    }

    @Test
    fun testCrowdLevelStringParsing() {
        assertEquals(CrowdLevel.LOW, CrowdLevel.fromString("LOW"))
        assertEquals(CrowdLevel.MEDIUM, CrowdLevel.fromString("MEDIUM"))
        assertEquals(CrowdLevel.MEDIUM, CrowdLevel.fromString("MODERATE"))
        assertEquals(CrowdLevel.HIGH, CrowdLevel.fromString("HIGH"))
        assertEquals(CrowdLevel.VERY_HIGH, CrowdLevel.fromString("VERY HIGH"))
        assertEquals(CrowdLevel.VERY_HIGH, CrowdLevel.fromString("VERY_HIGH"))
        assertEquals(CrowdLevel.VERY_HIGH, CrowdLevel.fromString("CRITICAL"))
        // Fallback for null or unknown string
        assertEquals(CrowdLevel.MEDIUM, CrowdLevel.fromString(null))
        assertEquals(CrowdLevel.MEDIUM, CrowdLevel.fromString("UNKNOWN_TIER"))
    }

    @Test
    fun testCrowdResponseDtoToDomainMapping() {
        val dto = CrowdResponseDto(
            destinationId = "darjeeling",
            destinationName = "Darjeeling",
            crowdScore = 88,
            crowdLevel = "VERY HIGH",
            colorCode = "#DC2626",
            summary = "Tiger Hill sunrise traffic & Mall road choke points at peak.",
            whyCrowded = listOf("Tiger Hill sunrise queue exceeding 2 hours", "Hotel room occupancy at 94%"),
            bottlenecks = listOf("Hill Cart Road Ghoom bend", "Chowrasta Mall entry"),
            factors = listOf(
                CrowdFactorDto(
                    name = "Traffic Congestion",
                    key = "traffic",
                    rawValue = 90f,
                    weightPercentage = 30,
                    weightedContribution = 27f,
                    description = "Heavy vehicular bottleneck on NH-110"
                )
            ),
            liveTrafficStatus = "Heavy Congestion",
            hotelOccupancyRate = "94%",
            lastUpdated = "2026-09-22T00:00:00Z",
            provenanceLabel = "REAL — DATABASE & TELEMETRY"
        )

        val domain = dto.toDomain(isLive = true)

        assertEquals("darjeeling", domain.destinationId)
        assertEquals(88, domain.crowdScore)
        assertEquals(CrowdLevel.VERY_HIGH, domain.crowdLevel)
        assertEquals("CRITICAL", domain.crowdLevel.displayName)
        assertTrue(domain.isLive)
        assertEquals("LIVE · Real-time Telemetry", domain.freshnessLabel)
        assertEquals(2, domain.whyCrowded.size)
        assertEquals(2, domain.bottlenecks.size)
        assertEquals(1, domain.factors.size)
        assertEquals("Traffic Congestion", domain.factors[0].name)
        assertEquals(90f, domain.factors[0].rawValue, 0.01f)
    }

    @Test
    fun testMissingFieldsDefaultSafely() {
        val minimalDto = CrowdResponseDto(
            destinationId = "kalimpong",
            destinationName = "Kalimpong",
            crowdScore = 42,
            crowdLevel = "" // missing level
        )

        val domain = minimalDto.toDomain(isLive = false, cachedAt = System.currentTimeMillis() - 120000)

        assertEquals(42, domain.crowdScore)
        // Verified fallback from score:
        assertEquals(CrowdLevel.MEDIUM, domain.crowdLevel)
        assertFalse(domain.isLive)
        assertTrue(domain.freshnessLabel.contains("Cached"))
        assertTrue(domain.whyCrowded.isEmpty())
        assertTrue(domain.bottlenecks.isEmpty())
        assertTrue(domain.factors.isEmpty())
    }

    @Test
    fun testDestinationSummaryMapping() {
        val dto = DestinationSummaryDto(
            id = "rishop",
            name = "Rishop",
            tagline = "Quiet Himalayan ridge",
            region = "Eastern Himalayas",
            state = "West Bengal",
            heroImage = "https://example.com/rishop.jpg",
            crowdScore = 18,
            crowdLevel = "LOW",
            avgCostPerDayInr = 2200,
            tags = listOf("Ecotourism", "Kanchenjunga")
        )

        val domain = dto.toDomain(isLive = true)

        assertEquals("rishop", domain.id)
        assertEquals(CrowdLevel.LOW, domain.crowdLevel)
        assertEquals("LOW", domain.crowdLevel.displayName)
        assertTrue(domain.isLive)
        assertEquals("LIVE", domain.freshnessLabel)
        assertEquals(2, domain.tags.size)
    }
}
