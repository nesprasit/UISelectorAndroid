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
package com.nesprasit.design.core.aniamtion

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener

/**
 * Created by (Nes) Maew.dev on 2020-01-20 23:52
 */

class AnimationManager {
    private val tag = this::class.java.simpleName

    private val PROPERTY_TRANSLATE = "PROPERTY_TRANSLATE"
    private val PROPERTY_ROTATE = "PROPERTY_ROTATE"
    private val PROPERTY_SCALE = "PROPERTY_SCALE"

    public var onAnimatorEnd: (() -> Unit)? = null
    public var onAnimationUpdated: (() -> Unit)? = null
    public var duration: Long = 50L
        set(value) {
            field = value
            if (::animation.isInitialized) animation.duration = value
        }

    private lateinit var animation: ValueAnimator

    private var propertyTranslate: PropertyValuesHolder
    private var propertyRotate: PropertyValuesHolder
    private var propertyScale: PropertyValuesHolder
    private val value: AnimationValue = AnimationValue()

    init {
        propertyTranslate = PropertyValuesHolder.ofFloat(PROPERTY_TRANSLATE, 0F, 0F)
        propertyRotate = PropertyValuesHolder.ofFloat(PROPERTY_ROTATE, 0F, 0F)
        propertyScale = PropertyValuesHolder.ofFloat(PROPERTY_SCALE, 0F, 0F)

        setUpAnimation()
    }

    private fun setUpAnimation() {
        animation = ValueAnimator()
        animation.setValues(propertyRotate, propertyScale, propertyTranslate)
        animation.duration = duration
        animation.interpolator = AccelerateDecelerateInterpolator()
        animation.addUpdateListener(animationUpdate())
        animation.addListener(onEnd = animationEnd())
    }

    private fun animationUpdate() = ValueAnimator.AnimatorUpdateListener {
        value.rotate = animation.getAnimatedValue(PROPERTY_ROTATE) as Float
        value.scale = animation.getAnimatedValue(PROPERTY_SCALE) as Float
        value.translate = animation.getAnimatedValue(PROPERTY_TRANSLATE) as Float

        onAnimationUpdated?.let { it() }
    }

    private fun animationEnd(): (animator: Animator) -> Unit = {
        onAnimatorEnd?.let { it() }
    }

    public fun getAnimation(): ValueAnimator {
        return animation
    }

    public fun setPropertyTranslate(vararg values: Float) {
        propertyTranslate.setFloatValues(values.getOrNull(0) ?: 0F, values.getOrNull(1) ?: 0F)
    }

    public fun setPropertyRotate(vararg values: Float) {
        propertyRotate.setFloatValues(values.getOrNull(0) ?: 0F, values.getOrNull(1) ?: 0F)
    }

    public fun setPropertyScale(vararg values: Float) {
        propertyScale.setFloatValues(values.getOrNull(0) ?: 0F, values.getOrNull(1) ?: 0F)
    }

    public fun setTranslate(rotate: Float) {
        value.translate = rotate
    }

    public fun getTranslate(): Float {
        return value.translate
    }

    public fun setScale(scale: Float) {
        value.scale = scale
    }

    public fun getScale(): Float {
        return value.scale
    }

    public fun setRotate(rotate: Float) {
        value.rotate = rotate
    }

    public fun getRotate(): Float {
        return value.rotate
    }
}






















