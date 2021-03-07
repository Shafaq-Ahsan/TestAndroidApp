package com.app.network_module

import android.content.Context

object NetworkModule {

    private lateinit var mContext: Context

    val context: Context
        get() = mContext

    fun initialize(context: Context) {
        mContext = context
    }
}