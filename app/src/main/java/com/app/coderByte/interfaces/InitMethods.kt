package com.app.coderByte.interfaces

import android.view.View

//An interface for fragments and activities
internal interface InitMethods : View.OnClickListener {

    fun getFragmentLayout(): Int

    fun getViewBinding()

    fun getViewModel()

    fun observe()

    fun setLiveDataValues()

    fun init()

    fun setListeners()

    fun setLanguageData()

}