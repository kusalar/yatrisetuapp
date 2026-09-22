package com.yatrisetu.app.domain.model

import com.yatrisetu.app.data.mapper.toDomain
import com.yatrisetu.app.data.mapper.toDto
import com.yatrisetu.app.data.mapper.toResponseDto
import com.yatrisetu.app.data.remote.dto.ActivitySlotDto
import com.yatrisetu.app.data.remote.dto.ItineraryDayDto
import com.yatrisetu.app.data.remote.dto.ItineraryResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ItineraryMappingTest {

    @Test
    fun testItineraryRequestDtoMapping() {
        val request = ItineraryRequest(
            destinationId = "darjeeling",
            durationDays = 7, // should be coerced to max 5
            travelerType = "SOLO",
            pace = "BALANCED",
            interests = listOf("Nature", "Culture"),
            budgetLevel = "MODERATE",
            startDate = "2026-10-01",
            optimizeForWeather = true
        )

        val dto = request.toDto()
        assertEquals("darjeeling", dto.destinationId)
        assertEquals(5, dto.durationDays) // coerced to 5
        assertEquals("SOLO", dto.travelerType)
        assertEquals("BALANCED", dto.pace)
        assertEquals(listOf("Nature", "Culture"), dto.interests)
        assertEquals("MODERATE", dto.budgetLevel)
        assertEquals("2026-10-01", dto.startDate)
        assertTrue(dto.optimizeForWeather)
    }

    @Test
    fun testItineraryResponseDtoToDomainMapping() {
        val activityDto1 = ActivitySlotDto(
            timeSlot = "08:30 - 11:00",
            period = "Morning",
            title = "Pineview Nursery Orchid Trail",
            description = "Walk through rare alpine orchid species.",
            locationName = "Kalimpong Ridge",
            crowdForecast = "Low crowd expected",
            costEstimateInr = 50,
            durationHrs = 2.5f,
            travelTip = "Visit before 10 AM",
            category = "Nature",
            imageUrl = "https://images.unsplash.com/orchid",
            isWeatherAdapted = true,
            adaptationReason = "Clear skies expected"
        )

        val activityDto2 = ActivitySlotDto(
            timeSlot = "12:00 - 14:30",
            period = "Afternoon",
            title = "Zang Dhok Palri Phodang",
            description = "Ancient Buddhist monastery on hilltop.",
            locationName = "Durpin Dara",
            crowdForecast = "Low",
            costEstimateInr = 0,
            durationHrs = 2.5f,
            travelTip = null,
            category = "Culture",
            imageUrl = null,
            isWeatherAdapted = false,
            adaptationReason = null
        )

        val dayDto1 = ItineraryDayDto(
            dayNumber = 1,
            theme = "Alpine Ridge & Monasteries",
            overview = "Exploring scenic hills and spiritual heritage",
            estimatedBudgetInr = 2500,
            activities = listOf(activityDto1, activityDto2),
            transitAdvice = "Local shared taxi recommended"
        )

        val responseDto = ItineraryResponseDto(
            itineraryId = "itin_klp_789",
            destinationId = "kalimpong",
            destinationName = "Kalimpong",
            durationDays = 1,
            travelerType = "Solo",
            pace = "Moderate",
            interests = listOf("Nature", "Culture"),
            totalEstimatedBudgetInr = 2500,
            crowdAvoidanceRating = "High",
            localEconomicImpactTag = "COMMUNITY_PREFERRED",
            days = listOf(dayDto1),
            aiGeneratedNote = "Optimized for ridge views",
            sustainabilityScore = 92,
            sustainabilityClassification = "EXCELLENT",
            weatherAdaptationNotice = "Outdoor morning slot synchronized with clear mountain skies.",
            whyThisItinerary = listOf("High biodiversity", "Low crowd density"),
            aiProviderUsed = "rule_based_fallback",
            optimizationHistory = listOf("AVOID_CROWDS")
        )

        val domain = responseDto.toDomain(isLive = true)

        assertEquals("itin_klp_789", domain.itineraryId)
        assertEquals("kalimpong", domain.destinationId)
        assertEquals("Kalimpong", domain.destinationName)
        assertEquals(1, domain.durationDays)
        assertEquals(92, domain.sustainabilityScore)
        assertEquals("High", domain.crowdAvoidanceRating)
        assertEquals("Outdoor morning slot synchronized with clear mountain skies.", domain.weatherAdaptationNotice)
        assertEquals(2500, domain.totalEstimatedBudgetInr)
        assertTrue(domain.isLive)
        assertEquals(listOf("AVOID_CROWDS"), domain.optimizationHistory)

        // Day assertion
        assertEquals(1, domain.days.size)
        val day = domain.days[0]
        assertEquals(1, day.dayNumber)
        assertEquals("Alpine Ridge & Monasteries", day.theme)
        assertEquals("Exploring scenic hills and spiritual heritage", day.overview)
        assertEquals(2, day.activities.size)

        // Activity with full fields
        val act1 = day.activities[0]
        assertEquals("Pineview Nursery Orchid Trail", act1.title)
        assertEquals("Kalimpong Ridge", act1.locationName)
        assertEquals("Low crowd expected", act1.crowdForecast)
        assertEquals(50, act1.costEstimateInr)
        assertEquals("Visit before 10 AM", act1.travelTip)
        assertEquals("Clear skies expected", act1.adaptationReason)
        assertTrue(act1.isWeatherAdapted)

        // Activity with null/optional fields
        val act2 = day.activities[1]
        assertEquals("Zang Dhok Palri Phodang", act2.title)
        assertEquals(0, act2.costEstimateInr)
        assertNull(act2.travelTip)
        assertNull(act2.adaptationReason)
        assertFalse(act2.isWeatherAdapted)
    }

    @Test
    fun testItineraryRoundTripDtoMapping() {
        val original = Itinerary(
            itineraryId = "itin_101",
            destinationId = "darjeeling",
            destinationName = "Darjeeling",
            durationDays = 2,
            travelerType = "Solo",
            pace = "Moderate",
            interests = listOf("Heritage"),
            totalEstimatedBudgetInr = 3000,
            crowdAvoidanceRating = "Good",
            localEconomicImpactTag = "COMMUNITY",
            days = listOf(
                ItineraryDay(
                    dayNumber = 1,
                    theme = "Colonial Charm",
                    overview = "Gentle introduction to town",
                    estimatedBudgetInr = 1500,
                    activities = listOf(
                        ActivitySlot(
                            timeSlot = "09:00",
                            period = "Morning",
                            title = "Mall Road Morning Stroll",
                            description = "Pedestrian plaza with mountain views",
                            locationName = "Chowrasta",
                            crowdForecast = "Calm before 10:30 AM",
                            costEstimateInr = 0,
                            durationHrs = 1.5f,
                            travelTip = null,
                            category = "Heritage",
                            imageUrl = null,
                            isWeatherAdapted = false,
                            adaptationReason = null
                        )
                    ),
                    transitAdvice = "Walking only"
                )
            ),
            aiGeneratedNote = "Standard plan",
            sustainabilityScore = 85,
            sustainabilityClassification = "GOOD",
            weatherAdaptationNotice = null,
            whyThisItinerary = listOf("Iconic landmarks"),
            aiProviderUsed = "rule_based_fallback",
            optimizationHistory = emptyList(),
            isLive = true
        )

        val dto = original.toResponseDto()
        assertEquals(original.itineraryId, dto.itineraryId)
        assertEquals(original.destinationId, dto.destinationId)
        assertEquals(original.days.size, dto.days.size)
        assertEquals(original.days[0].activities[0].title, dto.days[0].activities[0].title)

        val backToDomain = dto.toDomain()
        assertEquals(original.itineraryId, backToDomain.itineraryId)
        assertEquals(original.destinationName, backToDomain.destinationName)
        assertEquals(original.days[0].activities[0].costEstimateInr, backToDomain.days[0].activities[0].costEstimateInr)
    }

    @Test
    fun testAIAuthorityIntegrity() {
        // Assert that domain models are data holders and do not contain embedded LLMs, API keys or secret prompts
        val fields = Itinerary::class.java.declaredFields.map { it.name }
        assertFalse(fields.any { it.contains("apiKey", ignoreCase = true) })
        assertFalse(fields.any { it.contains("openAi", ignoreCase = true) })
        assertFalse(fields.any { it.contains("claude", ignoreCase = true) })
        assertFalse(fields.any { it.contains("gemini", ignoreCase = true) })
    }

    /**
     * Regression test: Groq LLM may emit ActivitySlot objects with missing required fields.
     * ActivitySlotDto.toDomain() must apply defensive fallbacks so the domain model always
     * holds valid values. ActivitySlot.toDto() must then produce non-blank strings for all
     * Pydantic-required fields, preventing HTTP 422 on optimize calls.
     */
    @Test
    fun testGroqMissingFieldsDefensiveFallback() {
        // Simulate a Groq-generated activity with blank/zero required fields
        val groqMissingFieldsDto = ActivitySlotDto(
            timeSlot = "08:30 AM - 10:00 AM",
            period = "",           // Groq omitted period
            title = "Tea Garden Walk",
            description = "Walk through tea gardens",
            locationName = "Mirik Tea Estate",
            crowdForecast = "",    // Groq omitted crowd_forecast
            costEstimateInr = 150,
            durationHrs = 0f,      // Groq omitted duration_hrs
            travelTip = null,
            category = "",         // Groq omitted category
            imageUrl = null,
            isWeatherAdapted = false,
            adaptationReason = null
        )

        val domain = groqMissingFieldsDto.toDomain()

        // period should be inferred from 08: prefix → "Morning"
        assertEquals("Morning", domain.period)
        // crowd_forecast should default to "Moderate"
        assertEquals("Moderate", domain.crowdForecast)
        // duration_hrs should default to 1.5f
        assertEquals(1.5f, domain.durationHrs, 0.001f)
        // category should default to "Culture"
        assertEquals("Culture", domain.category)

        // Now round-trip back to DTO for optimize request serialization
        val backToDto = domain.toDto()
        // All required Pydantic fields must be non-blank
        assertTrue("period must be non-blank", backToDto.period.isNotBlank())
        assertTrue("crowd_forecast must be non-blank", backToDto.crowdForecast.isNotBlank())
        assertTrue("category must be non-blank", backToDto.category.isNotBlank())
        assertTrue("duration_hrs must be positive", backToDto.durationHrs > 0f)
    }

    @Test
    fun testActivitySlotToDtoPreservesValidFields() {
        // Verify that valid field values are not overwritten by fallback logic
        val fullDto = ActivitySlotDto(
            timeSlot = "14:00 PM - 16:00 PM",
            period = "Afternoon",
            title = "Monastery Visit",
            description = "Explore ancient monastery",
            locationName = "Bokar Monastery",
            crowdForecast = "Low",
            costEstimateInr = 100,
            durationHrs = 2.0f,
            travelTip = "Remove shoes",
            category = "Culture",
            imageUrl = "https://example.com/img.jpg",
            isWeatherAdapted = true,
            adaptationReason = "Rain expected"
        )

        val domain = fullDto.toDomain()
        assertEquals("Afternoon", domain.period)
        assertEquals("Low", domain.crowdForecast)
        assertEquals(2.0f, domain.durationHrs, 0.001f)
        assertEquals("Culture", domain.category)

        val roundTripped = domain.toDto()
        assertEquals("Afternoon", roundTripped.period)
        assertEquals("Low", roundTripped.crowdForecast)
        assertEquals(2.0f, roundTripped.durationHrs, 0.001f)
        assertEquals("Culture", roundTripped.category)
    }

    @Test
    fun testTransitAdviceFallbackInDayDto() {
        // Verify ItineraryDay.toDto() applies transit_advice fallback when blank
        val dayWithBlankTransit = ItineraryDay(
            dayNumber = 1,
            theme = "Adventure Day",
            overview = "Trekking through ridges",
            estimatedBudgetInr = 1200,
            activities = emptyList(),
            transitAdvice = ""  // Groq may omit transit_advice
        )

        val dto = dayWithBlankTransit.toDto()
        assertTrue("transit_advice must be non-blank", dto.transitAdvice.isNotBlank())
        assertTrue("transit_advice should contain 'transit'", dto.transitAdvice.contains("transit", ignoreCase = true))
    }
}
