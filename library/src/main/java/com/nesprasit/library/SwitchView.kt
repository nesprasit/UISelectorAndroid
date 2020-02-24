/*
 * Copyright (C) 2020 Nesprasit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nesprasit.library

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.nesprasit.library.core.ViewValue
import com.nesprasit.library.core.aniamtion.AnimationManager
import com.nesprasit.library.extension.dpTopx

/**
 * Created by Nesprasit (https://Maew.dev) on 2020-01-24 15:30
 */

class SwitchView : View ,View.OnTouchListener{
    private val tag = this::class.java.simpleName

    private val animation: AnimationManager =
        AnimationManager()
    private val value: ViewValue =
        ViewValue()

    private lateinit var paintBackground: Paint
    private lateinit var paintSwitch: Paint
    private lateinit var rectangleBackground: RectF
    private lateinit var rectangleSwitch: RectF

    private var paddingBackground:Float = 0F
    private var radiusSwitch: Float = 0F

    private var offsetX:Float = 0F
    private var offsetY:Float = 0F

    private var isScaleRight:Boolean = true
    private var isScaleLeft:Boolean = false
    private var isScaleRightFocused:Boolean = true
    private var isScaleLeftFocused:Boolean = false

    private val selectedColor: Int = Color.parseColor("#a3ca76")
    private val unSelectedColor: Int = Color.parseColor("#c8c8c8")

    private var isPerformClick:Boolean = true

    constructor(context: Context) :
            super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) :
            super(context, attrs) {
        initAttrs(context, attrs, 0, 0)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        initAttrs(context, attrs, defStyleAttr, 0)
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes) {
        initAttrs(context, attrs, defStyleAttr, defStyleRes)
        init()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet,
                          defStyleAttr: Int, defStyleRes: Int) {

        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SwitchView,
            defStyleAttr,
            defStyleRes)

        try {
            value.isSelector = a.getBoolean(R.styleable.SwitchView_checked,value.isSelector)
            value.isEnable = a.getBoolean(R.styleable.SwitchView_enabled,true)
        }finally {
            a.recycle()
        }
    }

    private fun init() {
        paintBackground = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBackground.style = Paint.Style.FILL_AND_STROKE
        paintBackground.color = unSelectedColor

        paintSwitch = Paint(Paint.ANTI_ALIAS_FLAG)
        paintSwitch.style = Paint.Style.FILL_AND_STROKE
        paintSwitch.color = Color.WHITE

        rectangleBackground = RectF()
        rectangleSwitch = RectF()

        animation.duration = 100
        animation.setScale(0F)
        animation.setTranslate(0F)
        animation.onAnimationUpdated = { invalidate() }

        this.isEnabled = value.isEnable
        setBackgroundColor(Color.TRANSPARENT)
        setOnTouchListener(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        value.viewWidth = w
        value.viewHeight = h
        value.centerX = w / 2F
        value.centerY = h / 2F

        paddingBackground =  (w / 100F) * 1F

        value.top = paddingBackground
        value.left = paddingBackground
        value.right = w - paddingBackground
        value.bottom = h - paddingBackground
        value.radius = (h / 2) - paddingBackground

        radiusSwitch = (h / 2) - paddingBackground
        radiusSwitch = (radiusSwitch / 100F) * 85F
        radiusSwitch = radiusSwitch.dpTopx(context)

        rectangleBackground.set(
            value.top,
            value.left,
            value.right,
            value.bottom)

        rectangleSwitch.set(
            0F,
            0F,
            radiusSwitch,
            radiusSwitch)

        rectangleSwitch.offset(
            (value.centerX / 2) - (radiusSwitch / 2F),
            value.centerY - (radiusSwitch / 2F))

        offsetX = rectangleSwitch.left
        offsetY = rectangleSwitch.right

        if(value.isSelector) onSelected()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            // draw background
            canvas.drawRoundRect(rectangleBackground, value.radius, value.radius, paintBackground)

            val translate = animation.getTranslate()
            val scale = animation.getScale()

            // scale right
            if(isScaleRight){
                if(isScaleRightFocused){
                    rectangleSwitch.right = offsetY + scale * 33
                }else{
                    rectangleSwitch.right = (rectangleSwitch.left - offsetX) + offsetY
                }

            }else
            // scale left
            if(isScaleLeft){
                if(isScaleLeftFocused){
                    rectangleSwitch.left = offsetX + scale * 33
                }else{
                    rectangleSwitch.left = (rectangleSwitch.right - offsetY) + offsetX
                }
            }

            canvas.translate(translate,0F)
            canvas.drawRoundRect(rectangleSwitch,value.radius,value.radius,paintSwitch)
        }
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val x = event?.x ?: 0F
        val y = event?.y ?: 0F

        if(!this.isEnabled) return true

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if(!value.isSelector){
                    isScaleRight = true
                    isScaleRightFocused = true

                    isScaleLeft = false
                    isScaleLeftFocused = false

                    onFocused()
                }else{
                    isScaleLeft = true
                    isScaleLeftFocused = true

                    isScaleRight = false
                    isScaleRightFocused = false

                    onUnFocused()
                }
            }
            MotionEvent.ACTION_UP -> {
                if(x < value.viewWidth.toFloat() && y < value.viewHeight.toFloat()){
                    if(!value.isSelector){
                        onSelected()
                    }else{
                        onUnselected()
                    }

                    if(this.isEnabled && isPerformClick)
                        this.click(isPerformTouch = false)
                }else{
                    if(isScaleRight){
                        onUnFocused()
                    }else{
                        onFocused()
                    }
                }

                isPerformClick = true
            }
        }

        return true
    }

    private fun onFocused(){
        animation.setPropertyScale(0F,1F)
        animation.getAnimation().start()
    }

    private fun onUnFocused(){
        animation.setPropertyScale(1F,0F)
        animation.getAnimation().start()
    }

    private fun onSelected(){
        val destination = getPointXMax()

        animation.setPropertyScale(1F,1F)
        animation.setPropertyTranslate(0F,destination)
        animation.getAnimation().start()

        onSelectedScaleDown(destination)
    }

    private fun onSelectedScaleDown(destination:Float){
        value.isSelector = true

        animation.onAnimatorEnd = {
            animation.onAnimatorEnd = null
            animation.setTranslate(destination)
            animation.setPropertyTranslate(destination,destination)

            // scale down left to right
            isScaleLeft = true
            isScaleLeftFocused = false

            isScaleRight = false

            animation.setPropertyScale(1F,0F)
            animation.getAnimation().start()

            animation.onAnimatorEnd = {
                animation.onAnimatorEnd = null
                paintBackground.color = selectedColor
            }
        }
    }

    private fun onUnselected(){
        animation.setPropertyScale(0F,0F)
        animation.setPropertyTranslate(getPointXMax(),0F)
        animation.getAnimation().start()

        onUnselectedScaleDown()
    }

    private fun onUnselectedScaleDown(){
        value.isSelector = false

        animation.onAnimatorEnd = {
            animation.onAnimatorEnd = null
            animation.setTranslate(0F)
            animation.setPropertyTranslate(0F,0F)

            // scale down right to left
            isScaleLeft = false

            isScaleRight = true
            isScaleRightFocused = false

            animation.setPropertyScale(0F,1F)
            animation.getAnimation().start()

            animation.onAnimatorEnd = {
                animation.onAnimatorEnd = null
                paintBackground.color = unSelectedColor
            }
        }
    }

    private fun getPointXMax():Float{
        val maxScale = (offsetY + 1 * 33) + offsetX
        return value.viewWidth - maxScale
    }


    public fun isSelector():Boolean{
        return value.isSelector
    }

    public fun setSelector(selector:Boolean){
        value.isSelector = selector
        if(value.isSelector){
            onSelected()
        }else{
            onUnselected()
        }
    }

    public fun click(isPerformTouch:Boolean = true){
        if(isPerformTouch){
            isPerformClick = false
            performTouch()
        }

        if(!isPerformTouch){
            this.performClick()
        }
    }

    private fun performTouch() {
        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis() + 100

        val actionDown = MotionEvent.ACTION_DOWN
        val actionUp = MotionEvent.ACTION_UP
        val metaState = 0

        this.post {
            onTouch(this, MotionEvent.obtain(
                downTime,
                eventTime,
                actionDown,
                value.centerX,
                value.centerY,
                metaState
            ))

            onTouch(this, MotionEvent.obtain(
                downTime,
                eventTime,
                actionUp,
                value.centerX,
                value.centerY,
                metaState
            ))

            this.performClick()
        }

    }
}