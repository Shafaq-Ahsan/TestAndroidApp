package com.app.coderByte.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var binding: ViewDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }
    private fun init() {
        binding = DataBindingUtil.setContentView(this, getActivityLayout())
        getViewBinding()
    }
    abstract fun getActivityLayout(): Int
    override fun onResume() {
        super.onResume()
        try {
            attachBaseContext(this)
        }catch (e:Exception){

        }
    }
    abstract fun getViewBinding()




}