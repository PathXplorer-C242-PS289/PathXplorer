package com.example.pathxplorer.data.remote.retrofit

import com.example.pathxplorer.data.remote.response.LoginWithCredentialResponse
import com.example.pathxplorer.data.remote.response.RecommendationRiasecResponse
import com.example.pathxplorer.data.remote.response.RegisterWithCredentialResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("api/auth/register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterWithCredentialResponse

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