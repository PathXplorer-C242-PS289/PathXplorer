package com.example.pathxplorer.data.remote.retrofit

import com.example.pathxplorer.data.remote.response.LoginWithCredentialResponse
import com.example.pathxplorer.data.remote.response.RecommendationRiasecResponse
import com.example.pathxplorer.data.remote.response.RegisterWithCredentialResponse
import com.google.android.gms.fido.u2f.api.common.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/register")
    @Headers("Content-Type: application/json")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterWithCredentialResponse

    data class RegisterRequest(
        val email: String,
        val password: String
    )

    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginWithCredentialResponse

    @FormUrlEncoded
    @POST("api/auth/verify")
    suspend fun verifyOtp(
        @Field("email") email: String,
        @Field("otp") otp: String
    )

    @GET("/api/riasec/recommendation/{code}")
    suspend fun getRecommendation(
        @Path("code") code: String
    ): RecommendationRiasecResponse

}
