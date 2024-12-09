package com.example.pathxplorer.data.remote.response

import com.google.gson.annotations.SerializedName

data class RecommendationRiasecResponse(
	@field:SerializedName("riasec_type")
	val riasecType: String? = null,

	@field:SerializedName("key_skills")
	val keySkills: String? = null,

	@field:SerializedName("interest_description")
	val interestDescription: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("example_careers")
	val exampleCareers: String? = null
)

