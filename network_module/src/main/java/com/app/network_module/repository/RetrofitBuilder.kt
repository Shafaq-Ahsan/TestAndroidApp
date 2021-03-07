package com.app.network_module.repository

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.app.network_module.BuildConfig
import com.app.network_module.NetworkModule
import com.app.network_module.utils.Constants
import com.app.network_module.utils.Enums
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

internal object RetrofitBuilder {

    private val retrofitHashMap = HashMap<String, RetrofitAPI>()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun getRetrofitInstance(url: Enums.RetrofitBaseUrl): RetrofitAPI {

        val baseUrl = url.baseUrl

        if (!retrofitHashMap.containsKey(baseUrl) ||
            retrofitHashMap[baseUrl] == null
        ) {

            synchronized(this) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .client(
                        getOkHttpClient(
                            NetworkModule.context,
                            enableNetworkInterceptor(baseUrl)
                        )
                    )

                val restAPI = retrofit.build().create<RetrofitAPI>(RetrofitAPI::class.java)

                retrofitHashMap[baseUrl] = restAPI

                return restAPI
            }
        }

        return retrofitHashMap[baseUrl]!!
    }

    private fun enableNetworkInterceptor(baseUrl: String): Boolean {
        return baseUrl == Enums.RetrofitBaseUrl.BASE_URL.baseUrl
    }

    private fun getOkHttpClient(context: Context, isHostUrl: Boolean): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            level = Constants.LOG_LEVEL_API
        }

        val builder = OkHttpClient.Builder()
            .addInterceptor(ChuckInterceptor(context))
            .connectTimeout(Constants.API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.API_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.API_WRITE_TIMEOUT, TimeUnit.SECONDS)

        if (isHostUrl)
            builder.addNetworkInterceptor(NetworkInterceptorBSecure(context))


        builder.addInterceptor(interceptor)


        return builder.build()
    }

    private class NetworkInterceptorBSecure(private val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val builder = original.newBuilder()
            val request = builder
                .addHeader("x-device-type", "android")
                .addHeader("secret", Constants.API_SECRET)
                .addHeader("Accept-Encoding", "identity")
                .removeHeader(RetrofitAPI.HEADER_TAG)
                .method(original.method, original.body)
                .build()

            val response = chain.proceed(request)
            val tx = response.sentRequestAtMillis
            val rx = response.receivedResponseAtMillis
            val seconds = (rx - tx) / 1000.0
            if (!BuildConfig.BUILD_TYPE.equals("release", ignoreCase = true)) {
                try {
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        try {
                            Toast.makeText(
                                context,
                                "response time : $seconds secs",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {

                        }
                    }
                } catch (e: Exception) {

                }
            }
            return response
        }

    }

}