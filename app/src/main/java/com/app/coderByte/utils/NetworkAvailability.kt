package com.app.coderByte.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest

internal class NetworkAvailability private constructor() {
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: NetworkCallback? = null
    fun registerNetworkAvailability(
        context: Context,
        networkAvailabilityReceiver: BroadcastReceiver?
    ) {
        context.registerReceiver(
            networkAvailabilityReceiver, IntentFilter(
                NETWORK_AVAILABILITY_ACTION
            )
        )
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder = NetworkRequest.Builder()
        networkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                context.sendBroadcast(getNetworkAvailabilityIntent(true))
            }

            override fun onLost(network: Network) {
                context.sendBroadcast(getNetworkAvailabilityIntent(false))
            }
        }
        connectivityManager?.registerNetworkCallback(builder.build(), networkCallback)
        if (isAvailable(context)) {
            context.sendBroadcast(getNetworkAvailabilityIntent(true))
        } else {
            context.sendBroadcast(getNetworkAvailabilityIntent(false))
        }
    }

    fun unregisterNetworkAvailability(
        context: Context,
        networkAvailabilityReceiver: BroadcastReceiver?
    ) {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
        context.unregisterReceiver(networkAvailabilityReceiver)
    }


    private fun getNetworkAvailabilityIntent(isNetworkAvailable: Boolean): Intent {
        val intent = Intent(NETWORK_AVAILABILITY_ACTION)
        intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, !isNetworkAvailable)
        return intent
    }

    companion object {
        /* TODO: CHANGE THE ACTION NAME TO BE RELEVANT TO YOUR PROJECT */
        const val NETWORK_AVAILABILITY_ACTION = "com.foo.NETWORK_AVAILABILITY_ACTION"
        var instance: NetworkAvailability? = null
            get() {
                if (field == null) {
                    field = NetworkAvailability()
                }
                return field
            }
            private set

        fun isAvailable(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
    }
}