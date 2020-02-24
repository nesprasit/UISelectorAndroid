package com.nesprasit.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nesprasit.RadioButton
import kotlinx.android.synthetic.main.activity_radio.*

/**
 * Created by Nesprasit (https://Maew.dev) on 2020-02-03 19:03
 */

class RadioButtonActivity : AppCompatActivity(){
    private val tag = this::class.java.simpleName

    private var radioIndex:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radio)

        val radios = arrayListOf<RadioButton>(
            radio1,radio2,radio3
        )

        radios.forEachIndexed { index,radio ->
            radio.setOnClickListener {
                if(radioIndex != -1 && radioIndex != index){
                    radios[radioIndex].setSelector(false)
                }

                radioIndex = index
            }
        }
    }

}