package com.app.network_module.repository



import com.app.network_module.models.response.DataResponse
import com.app.network_module.models.response.ResponseGeneralArray
import com.app.network_module.utils.Enums
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Query

object ApiRepository {

    private val api = RetrofitBuilder.getRetrofitInstance(Enums.RetrofitBaseUrl.BASE_URL) // retrofit builder instance
    // call api method
    suspend fun callGetData(): Result<Response<ResponseGeneralArray<DataResponse>>> {
        return try {
            Success(api.getData())
        } catch (e: Exception) {
            Error(e)
        }
    }


}