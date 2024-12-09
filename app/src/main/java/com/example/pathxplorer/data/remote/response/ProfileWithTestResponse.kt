package com.example.pathxplorer.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileWithTestResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("success")
	val success: Boolean
)

data class TestResultsItem(

	@field:SerializedName("riasec_type")
	val riasecType: String,

	@field:SerializedName("key_skills")
	val keySkills: String,

	@field:SerializedName("interest_description")
	val interestDescription: String,

	@field:SerializedName("category")
	val category: String,

	@field:SerializedName("example_careers")
	val exampleCareers: String,

	@field:SerializedName("test_id")
	val testId: Int,

	@field:SerializedName("timestamp")
	val timestamp: String
)

data class Data(

	@field:SerializedName("testResults")
	val testResults: List<TestResultsItem>,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("verified_at")
	val verifiedAt: String,

	@field:SerializedName("email")
	val email: String
)
