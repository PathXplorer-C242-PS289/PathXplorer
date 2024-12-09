package com.example.pathxplorer.data.remote.response

data class LoginWithGoogleResponse(
	val message: String,
	val user: UserGoogle
)

data class UserGoogle(
	val providerType: String,
	val id: Int,
	val email: String
)

