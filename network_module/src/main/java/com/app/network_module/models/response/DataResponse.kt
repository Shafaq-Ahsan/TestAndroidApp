package com.app.network_module.models.response

import com.squareup.moshi.Json

data class DataResponse(

	@Json(name="uid")
	val uid: String? = "",

	@Json(name="price")
	val price: String? = "",

	@Json(name="name")
	val name: String? = "",

	@Json(name="created_at")
	val createdAt: String? = "",

	@Json(name="image_ids")
	val imageIds: List<String?>? = null,

	@Json(name="image_urls")
	val imageUrls: List<String?>? = null,

	@Json(name="image_urls_thumbnails")
	val imageUrlsThumbnails: List<String?>? = null
)
