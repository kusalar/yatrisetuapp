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
}
