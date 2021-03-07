package com.app.network_module.utils

import com.app.network_module.BuildConfig


internal object Enums {
    enum class RetrofitBaseUrl(val baseUrl: String) {
        BASE_URL(BuildConfig.BASE_URL)
    }

}