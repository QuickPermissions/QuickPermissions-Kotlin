package com.livinglifetechway.quickpermissions_sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.livinglifetechway.k4kotlin.onClick
import com.livinglifetechway.k4kotlin.setBindingView
import com.livinglifetechway.quickpermissions_sample.databinding.ActivityMainBinding
import com.livinglifetechway.quickpermissions_sample.kotlin.AllKotlinActivity
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_main)

        mBinding.buttonKotlinAll.onClick {
            startActivity<AllKotlinActivity>()
        }

    }
}
