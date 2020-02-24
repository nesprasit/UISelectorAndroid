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
package com.nesprasit

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.nesprasit.core.ViewValue
import com.nesprasit.core.aniamtion.AnimationManager
import com.nesprasit.library.R

/**
 * Created by Nesprasit (https://Maew.dev) on 2020-01-21 00:38
 */

class RadioButton : View, View.OnTouchListener {
    private val tag = this::class.java.simpleName

    private val animation: AnimationManager =
        AnimationManager()
    private val value: ViewValue =
        ViewValue()

    private lateinit var paint: Paint
    private val selectedColor: Int = Color.parseColor("#a3ca76")
    private val unSelectedColor: Int = Color.parseColor("#c8c8c8")

    private val scaleDefault: Float = 0.8F
    private val scaleMax: Float = 1F
    private val scaleMin: Float = 0.3F

    private var isPerformClick:Boolean = false

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
            R.styleable.RadioButton,
            defStyleAttr,
            defStyleRes)

        try {
            value.strokeWidth = a.getDimension(R.styleable.RadioButton_stroke_width,10F)
            value.isSelector = a.getBoolean(R.styleable.RadioButton_checked,value.isSelector)
            value.isEnable = a.getBoolean(R.styleable.RadioButton_enabled,true)
        }finally {
            a.recycle()
        }
    }

    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        setUnselectedPaint()

        animation.setScale(scaleDefault)
        animation.onAnimationUpdated = { invalidate() }

        setOnTouchListener(this)
        this.isEnabled = value.isEnable
        this.setLayerType(LAYER_TYPE_SOFTWARE, paint)

        if(value.isSelector) setSelector(value.isSelector)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        value.viewWidth = w
        value.viewHeight = h
        value.centerX = w / 2F
        value.centerY = h / 2F

        val percent = (value.viewWidth / 100F) * 7F
        value.padding = percent
        value.radius = value.centerX - value.strokeWidth - value.padding
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            val scale = animation.getScale()
            val centerX = value.centerX
            val centerY = value.centerY

            canvas.scale(scale, scale, centerX, centerY)
            canvas.drawCircle(centerX, centerY, value.radius, paint)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val x = event?.x ?: 0F
        val y = event?.y ?: 0F

        if(!this.isEnabled) return true

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!value.isSelector) onFocused()
            }
            MotionEvent.ACTION_CANCEL ->{
                onUnfocused()
            }
            MotionEvent.ACTION_UP -> {
                if (x > value.viewWidth && y > value.viewHeight) {
                    onUnfocused()
                } else {
                    if (!value.isSelector)
                        onSelected()

                    if(!isPerformClick && this.isEnabled)
                        this.click(false)
                }

                isPerformClick = false
            }
        }

        return true
    }

    override fun isEnabled(): Boolean {
        return value.isEnable
    }

    override fun setEnabled(enabled: Boolean) {
        if(!enabled) alpha = 0.5F
        value.isEnable = enabled
    }

    private fun setSelectedPaint() {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = selectedColor
        paint.strokeWidth = value.strokeWidth
        paint.setShadowLayer(15F, 0F, 0F, selectedColor)
    }

    private fun setUnselectedPaint() {
        paint.style = Paint.Style.STROKE
        paint.color = unSelectedColor
        paint.strokeWidth = value.strokeWidth
        paint.setShadowLayer(0F, 0F, 0F, Color.TRANSPARENT)
    }

    private fun onSelected() {
        value.isSelector = true

        animation.setPropertyScale(scaleMax, scaleDefault)
        animation.getAnimation().start()

        animation.onAnimatorEnd = {
            animation.onAnimatorEnd = null
            setSelectedPaint()

            animation.setPropertyScale(scaleMin, scaleDefault)
            animation.getAnimation().start()
        }

    }

    private fun onUnselected() {
        value.isSelector = false

        animation.setPropertyScale(scaleDefault, scaleMin)
        animation.getAnimation().start()

        animation.onAnimatorEnd = {
            animation.onAnimatorEnd = null
            setUnselectedPaint()

            animation.setPropertyScale(scaleMin, scaleDefault)
            animation.getAnimation().start()
        }
    }

    private fun onFocused() {
        setUnselectedPaint()

        animation.setPropertyScale(scaleDefault, scaleMax)
        animation.getAnimation().start()
    }

    private fun onUnfocused() {
        animation.setPropertyScale(scaleMax, scaleDefault)
        animation.getAnimation().start()
    }

    public fun isSelector():Boolean{
        return value.isSelector
    }

    public fun setSelector(selector:Boolean){
        value.isSelector = selector
        if(value.isSelector){
            setSelectedPaint()
            invalidate()
        }else{
            onUnselected()
        }
    }

    public fun click(isPerformTouch:Boolean = true){
        if(isPerformTouch){
            isPerformClick = true
            performTouch()
        }

        this.performClick()
    }

    private fun performTouch() {
        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis() + 100

        val actionDown = MotionEvent.ACTION_DOWN
        val actionUp = MotionEvent.ACTION_UP
        val metaState = 0

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
    }


}