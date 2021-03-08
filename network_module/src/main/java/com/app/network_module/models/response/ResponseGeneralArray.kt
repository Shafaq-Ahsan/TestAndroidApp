package com.app.network_module.models.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
// model class
@JsonClass(generateAdapter = true)
data class ResponseGeneralArray<T>(
    @Json(name = "message")
    val message: String? = null,
    @Json(name = "errors")
    val errors: List<String> = arrayListOf(),
    @Json(name = "status")
    val status_code: Int = 0,
    @Json(name = "results")
    var data: List<T>?,
    @Json(name = "pagination")
    val pagination: Any? = null
)