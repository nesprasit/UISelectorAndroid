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
package com.nesprasit.design.core

/**
 * Created by (Nes) Maew.dev on 2020-01-21 00:29
 */

data class ViewValue(
    var isEnable: Boolean = true,
    var isSelector: Boolean = false,
    var strokeWidth: Float = 0F,
    var viewWidth: Int = 0,
    var viewHeight: Int = 0,
    var centerX: Float = 0F,
    var centerY: Float = 0F,
    var padding: Float = 0F,
    var top: Float = 0F,
    var left: Float = 0F,
    var right: Float = 0F,
    var bottom: Float = 0F,
    var radius: Float = 0F
)