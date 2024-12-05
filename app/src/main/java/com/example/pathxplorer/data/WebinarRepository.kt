package com.example.pathxplorer.data

import com.example.pathxplorer.data.models.WebinarModel
import com.example.pathxplorer.service.webinar.WebinarRetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WebinarRepository private constructor() {
    suspend fun getWebinars(): Result<List<WebinarModel>> = withContext(Dispatchers.IO) {
        try {
            val response = WebinarRetrofitInstance.eventService.getActiveEvents()
            if (response.isSuccessful) {
                val webinars = response.body()?.listEvents ?: emptyList()
                Result.Success(webinars)
            } else {
                Result.Error("Failed to fetch webinars")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun getWebinarDetail(id: Int): Result<WebinarModel> = withContext(Dispatchers.IO) {
        try {
            val response = WebinarRetrofitInstance.eventService.getEventDetails(id)
            if (response.isSuccessful) {
                val webinar = response.body()?.event
                if (webinar != null) {
                    Result.Success(webinar)
                } else {
                    Result.Error("Webinar not found")
                }
            } else {
                Result.Error("Failed to fetch webinar details")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    companion object {
        @Volatile
        private var instance: WebinarRepository? = null

        fun getInstance(): WebinarRepository {
            return instance ?: synchronized(this) {
                instance ?: WebinarRepository().also { instance = it }
            }
        }
    }
}