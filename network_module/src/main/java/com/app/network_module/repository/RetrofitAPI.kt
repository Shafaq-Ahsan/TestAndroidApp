package com.app.network_module.repository


import com.app.network_module.models.response.DataResponse
import com.app.network_module.models.response.ResponseGeneralArray
import retrofit2.Response
import retrofit2.http.*

internal interface RetrofitAPI {

    companion object {
        const val HEADER_TAG = "@"
    }

    /*POST CALLS*/

    /*GET CALLS*/
    @GET("default/dynamodb-writer")
    suspend fun getData(): Response<ResponseGeneralArray<DataResponse>>

}