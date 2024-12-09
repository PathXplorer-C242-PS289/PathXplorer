package com.example.pathxplorer.data.remote.response

data class LoginWithCredentialResponse(
	val message: String,
	val user: UserCredential,
	val token: String
)

data class UserCredential(
	val providerType: String,
	val id: Int,
	val email: String,
	val username: String
)

