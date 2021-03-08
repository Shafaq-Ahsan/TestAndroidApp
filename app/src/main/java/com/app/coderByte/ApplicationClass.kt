package com.app.coderByte

import android.app.Application
import com.app.coderByte.models.language.LanguageJson
import com.app.coderByte.utils.*
import com.app.network_module.NetworkModule
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


internal class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        mApplicationClass = this
        setUpLanguageJson()
        NetworkModule.initialize(this.applicationContext)
    }

    companion object {

        @JvmStatic
        private lateinit var mApplicationClass: ApplicationClass

        @JvmStatic
        val application: ApplicationClass by lazy { mApplicationClass }
        //no hard coded string
        // todo:  upload the json file on server/firebase and download new string realtime even the app is on playstore
        @JvmStatic
        internal var languageJson: LanguageJson? = null


        fun setUpLanguageJson() {
            CoroutineScope(Dispatchers.IO).launch {
                languageJson = Gson().fromJson(
                    application.loadJSONFromAssets("AppAndroidEn.json"),
                    LanguageJson::class.java
                )
            }
        }
    }
}