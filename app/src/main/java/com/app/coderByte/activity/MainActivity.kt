package com.app.coderByte.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.app.coderByte.R
import com.app.coderByte.databinding.ActivityMainBinding
import com.app.coderByte.ApplicationClass
import com.app.coderByte.dialogs.LoaderFragment
import com.app.coderByte.utils.DisplayNotification
import com.app.coderByte.utils.NetworkAvailability
import com.app.coderByte.viewmodels.MainActivityViewModel

class MainActivity : BaseActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: MainActivityViewModel
    private lateinit var mNavController: NavController
    private lateinit var mNavDestination: NavDestination
    private var networkAvailability: NetworkAvailability? = null
    private val loaderFragment by lazy { LoaderFragment() }
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                mViewModel.callMessageNotification(
                    ApplicationClass.languageJson?.messages?.errorMessages?.internet ?: "",
                    DisplayNotification.STYLE.FAILURE
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        networkAvailability = NetworkAvailability.instance
        networkAvailability?.registerNetworkAvailability(this, receiver)
    }

    private fun init() {
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        initNavController()
        observe()
    }

    override fun onDestroy() {
        networkAvailability?.unregisterNetworkAvailability(this, receiver)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initNavController() {
        mNavController = findNavController(R.id.nav_host_fragment)
        mNavController.apply {
            addOnDestinationChangedListener { _, destination, _ ->
                mNavDestination = destination
            }
        }
    }

    override fun getActivityLayout() = R.layout.activity_main

    override fun getViewBinding() {
        mBinding = binding as ActivityMainBinding
    }

    private fun observe() {
        mViewModel.apply {
            loader.observe(this@MainActivity, Observer { showLoader ->
                showLoader ?: return@Observer
                if (showLoader) {
                    if (loaderFragment.isVisible) {
                        loaderFragment.dismiss()
                    }
                    loaderFragment.isCancelable = false
                    val ft = supportFragmentManager.beginTransaction()
                    val prev = supportFragmentManager.findFragmentByTag("dialog")
                    if (prev != null) {
                        ft.remove(prev)
                    }
                    ft.addToBackStack(null)
                    ft.let { loaderFragment.show(it, "dialog") }
                } else {
                    try {
                        loaderFragment.dismiss()
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }

            })
            notificationMessage.observe(this@MainActivity, Observer {
                it ?: return@Observer
                if (it.show) {
                    DisplayNotification.show(
                        this@MainActivity,
                        mBinding.notificationLayout,
                        it.style,
                        it.message
                    )
                    setNotificationMessage(it.copy(show = false))
                }
            })
        }
    }

}