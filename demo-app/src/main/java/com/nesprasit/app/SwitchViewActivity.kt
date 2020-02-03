package com.nesprasit.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by Nesprasit (https://Maew.dev) on 2020-02-03 19:04
 */

class SwitchViewActivity : AppCompatActivity(){
    private val tag = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch)

    }

}