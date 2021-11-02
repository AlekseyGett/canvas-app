package com.github.alekseygett.canvasapp.feature.canvas.domain.models

import androidx.annotation.ColorRes

enum class COLOR(@ColorRes val value: Int) {
    BLACK(android.R.color.black),
    RED(android.R.color.holo_red_dark),
    GREEN(android.R.color.holo_green_dark),
    BLUE(android.R.color.holo_blue_dark);

    companion object {
        private val map = values().associateBy(COLOR::value)
        fun from(color: Int) = map[color] ?: BLACK
    }
}