package com.yatrisetu.app.data.remote

import com.yatrisetu.app.data.remote.dto.AlternativesResponseDto
import com.yatrisetu.app.data.remote.dto.CrowdResponseDto
import com.yatrisetu.app.data.remote.dto.DestinationDetailDto
import com.yatrisetu.app.data.remote.dto.DestinationSummaryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * YatriSetuApi
 * Retrofit REST interface consuming the authoritative Yatri Setu FastAPI backend.
 */
interface YatriSetuApi {

    @GET("destinations")
    suspend fun getDestinations(
        @Query("query") query: String? = null,
        @Query("crowd_level") crowdLevel: String? = null
    ): Response<List<DestinationSummaryDto>>

    @GET("destinations/{id}")
    suspend fun getDestinationDetails(
        @Path("id") id: String
    ): Response<DestinationDetailDto>

    @GET("destinations/{id}/crowd")
    suspend fun getDestinationCrowd(
        @Path("id") id: String
    ): Response<CrowdResponseDto>

    @GET("destinations/{id}/alternatives")
    suspend fun getDestinationAlternatives(
        @Path("id") id: String
    ): Response<AlternativesResponseDto>

    @retrofit2.http.POST("destinations/{id}/accept-alternative")
    suspend fun acceptAlternative(
        @Path("id") id: String,
        @retrofit2.http.Body body: com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceRequestDto
    ): Response<com.yatrisetu.app.data.remote.dto.AlternativeAcceptanceResponseDto>

    @retrofit2.http.POST("itinerary/generate")
    suspend fun generateItinerary(
        @retrofit2.http.Body request: com.yatrisetu.app.data.remote.dto.ItineraryRequestDto
    ): Response<com.yatrisetu.app.data.remote.dto.ItineraryResponseDto>

    @retrofit2.http.POST("itinerary/optimize")
    suspend fun optimizeItinerary(
        @retrofit2.http.Body request: com.yatrisetu.app.data.remote.dto.ItineraryOptimizeRequestDto
    ): Response<com.yatrisetu.app.data.remote.dto.ItineraryResponseDto>
}
