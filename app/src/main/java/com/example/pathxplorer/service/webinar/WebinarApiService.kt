package com.example.pathxplorer.service.webinar

import com.example.pathxplorer.data.models.WebinarDetailResponse
import com.example.pathxplorer.data.models.WebinarResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WebinarApiService {
    @GET("events")
    suspend fun getActiveEvents(
        @Query("active") active: Int = -1
    ): Response<WebinarResponse>

    @GET("events/{id}")
    suspend fun getEventDetails(
        @Path("id") eventId: Int
    ): Response<WebinarDetailResponse>
}