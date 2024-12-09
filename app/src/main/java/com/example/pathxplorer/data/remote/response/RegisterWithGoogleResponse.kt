package com.example.pathxplorer.data.remote.response

data class RegisterWithGoogleResponse(
	val message: String,
	val user: User
)

data class User(
	val providerType: String,
	val id: Int,
	val email: String,
	val username: String
)

