package com.app.network_module.utils

import com.app.network_module.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor


internal object Constants {

    val API_SECRET: String = ""
    val LOG_LEVEL_API = HttpLoggingInterceptor.Level.BODY

    const val API_CONNECT_TIMEOUT: Long = 10

    const val API_READ_TIMEOUT: Long = 10

    const val API_WRITE_TIMEOUT: Long = 10

}
