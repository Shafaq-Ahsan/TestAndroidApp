package com.app.coderByte.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.coderByte.ApplicationClass
import com.app.coderByte.models.helper.NotificationMessage
import com.app.coderByte.utils.DisplayNotification
import kotlinx.coroutines.*
import java.lang.Exception

open class BaseViewModel : ViewModel() {
    //mutable live data for loader
    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean>
        get() = _loader

    //mutable live data to show message from top
    private val _notificationMessage = MutableLiveData<NotificationMessage>()
    val notificationMessage: LiveData<NotificationMessage>
        get() = _notificationMessage

    // supervisor job variable
    private val job = SupervisorJob()

    private fun setLoader(flag: Boolean) {
        _loader.postValue(flag)
    }


    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

    protected val coroutineScope = CoroutineScope(job + Dispatchers.IO + coroutineExceptionHandler)

    //method called from coroutine to show loader
    open suspend fun toggleLoader(flag: Boolean) {
        withContext(Dispatchers.Main) {
            if (_loader.value != flag) {
                setLoader(flag)
            }
        }
    }

    //set notification message
    fun setNotificationMessage(message: NotificationMessage) {
        _notificationMessage.value = message
    }

    //call notification from coroutine
    fun callMessageNotification(
        msg: String,
        style: DisplayNotification.STYLE = DisplayNotification.STYLE.FAILURE
    ) {
        try {
            CoroutineScope(Dispatchers.Main).launch {

                setNotificationMessage(
                    NotificationMessage(message = msg, style = style)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //handle error messages from api calls
    protected fun showErrorMessage(throwable: Throwable) {
        throwable.printStackTrace()
        callMessageNotification(
            ApplicationClass.languageJson?.messages?.errorMessages?.internet ?: ""
        )
    }

    //handle error messages from api calls
    protected fun handleServerError(error: String) {
        if (error.isNotEmpty()) {
            callMessageNotification(error)
        } else {
            callMessageNotification(
                ApplicationClass.languageJson?.messages?.errorMessages?.internal ?: ""
            )
        }
    }

    // clear job
    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

}