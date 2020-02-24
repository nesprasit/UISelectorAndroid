package com.nesprasit.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    public fun clickToCheckBox(view: View) {
        startActivity(Intent(this,CheckBoxActivity::class.java))

    }

    public fun clickToRadioButton(view: View) {
        startActivity(Intent(this,RadioButtonActivity::class.java))
    }

    public fun clickToSwitchView(view: View) {
        startActivity(Intent(this,SwitchViewActivity::class.java))
    }
}
