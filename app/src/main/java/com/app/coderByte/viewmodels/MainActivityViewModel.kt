package com.app.coderByte.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.coderByte.utils.Constants
import com.app.network_module.models.response.DataResponse
import com.app.network_module.repository.ApiRepository
import com.app.network_module.repository.onError
import com.app.network_module.repository.onSuccess
import kotlinx.coroutines.launch

public open class MainActivityViewModel : BaseViewModel() {

    var _responseData = MutableLiveData<ArrayList<DataResponse>>()
    val responseData: LiveData<ArrayList<DataResponse>>
        get() = _responseData

    /*API SECTION*/

    fun getData() {
        coroutineScope.launch {
            toggleLoader(true)
            val data = ApiRepository.callGetData()
            data.onSuccess {
                toggleLoader(false)
                if (it.isSuccessful && it.code() == Constants.API_RESPONSE_CODE_200) {
                    _responseData.postValue(it.body()?.data as ArrayList<DataResponse>?)
                } else if (it.code() == Constants.API_RESPONSE_CODE_401) {
                    //Auth error or logout app
                    handleServerError(it.message())
                } else if (it.code() == Constants.API_RESPONSE_CODE_500) {
                    handleServerError(it.message())
                } else {
                    handleServerError(it.message())
                }
            }.onError {
                toggleLoader(false)
                showErrorMessage(it.exception)
            }
        }
    }


}