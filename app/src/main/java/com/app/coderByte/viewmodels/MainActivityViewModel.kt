package com.app.coderByte.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.coderByte.utils.Constants
import com.app.network_module.models.response.DataResponse
import com.app.network_module.repository.ApiRepository
import com.app.network_module.repository.onError
import com.app.network_module.repository.onSuccess
import kotlinx.coroutines.launch

 open class MainActivityViewModel : BaseViewModel() {

    var _responseData = MutableLiveData<ArrayList<DataResponse>>() //Mutable livedata for api list
    val responseData: LiveData<ArrayList<DataResponse>>
        get() = _responseData

    /*API SECTION*/

    fun getData() {
        coroutineScope.launch {
            toggleLoader(true) // show loader
            val data = ApiRepository.callGetData() // call api
            data.onSuccess {
                //on api success onSuccess is called
                toggleLoader(false) // hide loader
                if (it.isSuccessful && it.code() == Constants.API_RESPONSE_CODE_200) { // if response is 200
                    _responseData.postValue(it.body()?.data as ArrayList<DataResponse>?)
                } else if (it.code() == Constants.API_RESPONSE_CODE_401) { // if response is 401
                    //Auth error or logout app if needed
                    handleServerError(it.message()) // show message
                } else if (it.code() == Constants.API_RESPONSE_CODE_500) {
                    handleServerError(it.message()) // show message
                } else {
                    handleServerError(it.message()) // show message
                }
            }.onError {
                //on api failure onError is called
                toggleLoader(false) // hide loader
                showErrorMessage(it.exception) // show message
            }
        }
    }


}