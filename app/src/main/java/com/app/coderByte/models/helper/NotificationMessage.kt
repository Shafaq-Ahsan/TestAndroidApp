package com.app.coderByte.models.helper

import com.app.coderByte.utils.DisplayNotification

//helper class for notification from top
data class NotificationMessage(
    var show: Boolean = true,
    var message: String = "",
    var style: DisplayNotification.STYLE = DisplayNotification.STYLE.INFO
)