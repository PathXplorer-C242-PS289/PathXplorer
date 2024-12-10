package com.example.pathxplorer.data.remote.retrofit

import com.example.pathxplorer.data.remote.response.ForgotPasswordResponse
import com.example.pathxplorer.data.remote.response.LoginWithCredentialResponse
import com.example.pathxplorer.data.remote.response.LoginWithGoogleResponse
import com.example.pathxplorer.data.remote.response.ProfileWithTestResponse
import com.example.pathxplorer.data.remote.response.RecommendationRiasecResponse
import com.example.pathxplorer.data.remote.response.RegisterWithCredentialResponse
import com.example.pathxplorer.data.remote.response.RegisterWithGoogleResponse
import com.example.pathxplorer.data.remote.response.ResultTestResponse
import com.example.pathxplorer.data.remote.response.SaveRiasecTestResponse
import com.example.pathxplorer.data.remote.response.SendNewOtpResponse
import com.example.pathxplorer.data.remote.response.VerifyOtpResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // start api service authentication

    @POST("api/auth/register")
    @Headers("Content-Type: application/json")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterWithCredentialResponse

    data class RegisterRequest(
        val username: String,
        val email: String,
        val password: String
    )

    @POST("api/auth/verify")
    @Headers("Content-Type: application/json")
    suspend fun verifyOtp(
        @Body request: VerifyRequest
    ) : VerifyOtpResponse

    data class VerifyRequest(
        val email: String,
        val otp: String
    )

    @POST("api/auth/login")
    @Headers("Content-Type: application/json")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginWithCredentialResponse

    data class LoginRequest(
        val email: String,
        val password: String
    )

    @POST("api/auth/send-new-otp")
    @Headers("Content-Type: application/json")
    suspend fun sendNewOtp(
        @Body request: SendNewOtpRequest
    ): SendNewOtpResponse

    data class SendNewOtpRequest(
        val email: String
    )

    @POST("api/auth/register/google")
    @Headers("Content-Type: application/json")
    suspend fun registerWithGoogle(
        @Body request: AuthWithGoogleRequest
    ): RegisterWithGoogleResponse

    data class AuthWithGoogleRequest(
        val idToken: String
    )

    @POST("api/auth/login/google")
    @Headers("Content-Type: application/json")
    suspend fun loginWithGoogle(
        @Body request: AuthWithGoogleRequest
    ): LoginWithGoogleResponse

    @POST("api/auth/forgot-password")
    @Headers("Content-Type: application/json")
    suspend fun ForgotPassword(
        @Body request: forgotPasswordRequest
    ): ForgotPasswordResponse

    data class forgotPasswordRequest(
        val email: String
    )

    @POST("api/auth/verify-otp")
    @Headers("Content-Type: application/json")
    suspend fun verifyOtpForgotPassword(
        @Body request: VerifyRequest
    ): VerifyOtpResponse

    @POST("api/auth/reset-password")
    @Headers("Content-Type: application/json")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): VerifyOtpResponse

    data class ResetPasswordRequest(
        val email: String,
        val otp: String,
        val newPassword: String
    )

    // end api service authentication

    // start api service profile

    @GET("api/profile")
    suspend fun getProfile(): ProfileWithTestResponse

    // end api service profile


    // start api service riasec and test

    @GET("api/riasec/recommendation/{code}")
    suspend fun getRecommendation(
        @Path("code") code: String
    ): RecommendationRiasecResponse

    @GET("api/riasec/result/{testId}")
    suspend fun getResultTestbyId(
        @Path("testId") testId: String
    ): ResultTestResponse

    @POST("api/riasec/save-results")
    @Headers("Content-Type: application/json")
    suspend fun saveResultTest(
        @Body request: SaveTestRequest
    ): SaveRiasecTestResponse

    data class SaveTestRequest(
        val testId: Int,
        val userId: Int,
        val category: String
    )

    // end api service riasec and test

}
