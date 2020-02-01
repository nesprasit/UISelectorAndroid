/*
 * Copyright (C) 2020 (Nes) Maew.dev
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
package com.nes.uikit

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.nes.uikit.core.ViewValue
import com.nes.uikit.core.aniamtion.AnimationManager

/**
 * Created by (Nes) Maew.dev on 2020-01-21 01:25
 */

class CheckBox : View, View.OnTouchListener {
    private val tag = this::class.java.simpleName

    private val animation: AnimationManager = AnimationManager()
    private val value: ViewValue = ViewValue()

    private lateinit var selectedPaint: Paint
    private lateinit var unselectedPaint: Paint
    private lateinit var rectangle: RectF

    private lateinit var bitmapPaint: Paint
    private lateinit var bitmapCheck: Bitmap
    private lateinit var bitmapRectF: RectF

    private var size:Float = 0F
    private val selectedColor: Int = Color.parseColor("#a3ca76")
    private val unselectedColor: Int = Color.parseColor("#c8c8c8")

    private var isPerformClick: Boolean = true

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

    private fun initAttrs(
        context: Context, attrs: AttributeSet,
        defStyleAttr: Int, defStyleRes: Int
    ) {

        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CheckBox,
            defStyleAttr,
            defStyleRes
        )

        try {
            value.strokeWidth = a.getDimension(R.styleable.CheckBox_stroke_width, 7F)
            value.isSelector = a.getBoolean(R.styleable.CheckBox_checked, value.isSelector)
            value.isEnable = a.getBoolean(R.styleable.CheckBox_enabled, true)
            size = a.getDimension(R.styleable.CheckBox_size,0F)
        } finally {
            a.recycle()
        }
    }

    private fun init() {
        bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bitmapRectF = RectF()

        selectedPaint = getSelectedPain()
        unselectedPaint = getUnselectedPaint()
        rectangle = RectF()

        animation.duration = 150L
        animation.onAnimationUpdated = { invalidate() }

        bitmapCheck = createBitmapCheck()

        initEvent()
    }

    private fun initEvent() {
        setOnTouchListener(this)

        this.isEnabled = value.isEnable
        if (value.isSelector) setSelector(value.isSelector)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams.width = if(size <= 0){
            context.resources.getDimension(R.dimen.ui_design_checkbox_width).toInt()
        }else{
            size.toInt()
        }

        layoutParams.height = if(size <= 0){
            context.resources.getDimension(R.dimen.ui_design_checkbox_height).toInt()
        }else{
            size.toInt()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        value.viewWidth = w
        value.viewHeight = h
        value.centerX = w / 2F
        value.centerY = h / 2F

        var percent = (value.viewWidth / 100F) * 15F

        value.top = percent
        value.left = value.top
        value.right = w - value.left
        value.bottom = h - value.top

        rectangle.set(value.top, value.left, value.right, value.bottom)

        percent = (value.viewWidth / 100F) * 30F
        bitmapRectF.set(percent, percent, w - percent, h - percent)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val rotate = animation.getRotate()
        val scale = animation.getScale()
        val centerX = value.centerX
        val centerY = value.centerY

        canvas?.rotate(rotate, centerX, centerY)
        canvas?.drawRoundRect(rectangle, 2F, 2F, unselectedPaint)

        canvas?.scale(1F, scale, 1F, value.top)
        canvas?.drawRoundRect(rectangle, 2F, 2F, selectedPaint)

        canvas?.drawBitmap(bitmapCheck, null, bitmapRectF, bitmapPaint)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val x = event?.x ?: 0F
        val y = event?.y ?: 0F

        if (!this.isEnabled) return true

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (value.isSelector) {
                    onUnselected()
                } else {
                    onSelected()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (x > value.viewWidth && y > value.viewHeight) return true

                value.isSelector = !value.isSelector

                if (isPerformClick) {
                    this.click(false)
                }

                isPerformClick = true

            }
        }

        return true
    }

    override fun isEnabled(): Boolean {
        return value.isEnable
    }

    override fun setEnabled(enabled: Boolean) {
        if (!enabled) alpha = 0.5F
        value.isEnable = enabled
    }

    private fun getSelectedPain(): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = value.strokeWidth
        paint.color = selectedColor
        return paint
    }

    private fun getUnselectedPaint(): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = value.strokeWidth
        paint.color = unselectedColor
        return paint
    }

    private fun onSelected() {
        animation.setPropertyRotate(0F, 90F)
        animation.setPropertyScale(0F, 1F)

        unselectedPaint.color = selectedColor
        animation.getAnimation().start()
    }

    private fun onUnselected() {
        animation.setPropertyRotate(90F, 0F)
        animation.setPropertyScale(1F, 0F)

        unselectedPaint.color = unselectedColor
        animation.getAnimation().start()
    }

    public fun isSelector(): Boolean {
        return value.isSelector
    }

    public fun setSelector(selector: Boolean) {
        value.isSelector = selector
        if (value.isSelector) {
            onUnselected()
            post { onSelected() }
        } else {
            post { onUnselected() }
        }
    }

    public fun click(isPerformTouch: Boolean = true) {
        if (isPerformTouch) {
            isPerformClick = false
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

        onTouch(
            this, MotionEvent.obtain(
                downTime,
                eventTime,
                actionDown,
                value.centerX,
                value.centerY,
                metaState
            )
        )

        onTouch(
            this, MotionEvent.obtain(
                downTime,
                eventTime,
                actionUp,
                value.centerX,
                value.centerY,
                metaState
            )
        )
    }

    private fun createBitmapCheck(): Bitmap {
        val bitmap = ContextCompat.getDrawable(context, R.drawable.ic_done)!!.toBitmap()

        val matrix = Matrix()
        val width = bitmap.width
        val height = bitmap.height

        matrix.setRotate(-90F)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

}