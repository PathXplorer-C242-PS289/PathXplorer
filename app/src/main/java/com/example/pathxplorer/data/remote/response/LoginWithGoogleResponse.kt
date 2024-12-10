package com.example.pathxplorer.data.remote.response

data class LoginWithGoogleResponse(
	val message: String,
	val user: User,
	val token: String
)

data class UserGoogle(
	val providerType: String,
	val id: Int,
	val email: String,
	val username: String
)

