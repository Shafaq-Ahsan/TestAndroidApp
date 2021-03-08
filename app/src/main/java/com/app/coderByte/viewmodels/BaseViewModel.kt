package com.app.coderByte.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.coderByte.ApplicationClass
import com.app.coderByte.models.helper.NotificationMessage
import com.app.coderByte.utils.DisplayNotification
import kotlinx.coroutines.*
import java.lang.Exception

public open class BaseViewModel : ViewModel() {
    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean>
        get() = _loader

    private fun setLoader(flag: Boolean) {
        _loader.postValue( flag)
    }

    private val job = SupervisorJob()
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }
    protected val coroutineScope = CoroutineScope(job + Dispatchers.IO + coroutineExceptionHandler)
    open suspend fun toggleLoader(flag: Boolean) {
        withContext(Dispatchers.Main) {
            if (_loader.value != flag) {
                setLoader(flag)
            }
        }
    }

    private val _notificationMessage = MutableLiveData<NotificationMessage>()
    val notificationMessage: LiveData<NotificationMessage>
        get() = _notificationMessage

    fun setNotificationMessage(message: NotificationMessage) {
        _notificationMessage.value = message
    }

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

    protected fun showErrorMessage(throwable: Throwable) {
        throwable.printStackTrace()
        callMessageNotification(
            ApplicationClass.languageJson?.messages?.errorMessages?.internet ?: ""
        )
    }

    protected fun handleServerError(error: String) {
        if (error.isNotEmpty()) {
            callMessageNotification(error)
        } else {
            callMessageNotification(
                ApplicationClass.languageJson?.messages?.errorMessages?.internal ?: ""
            )
        }
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

}