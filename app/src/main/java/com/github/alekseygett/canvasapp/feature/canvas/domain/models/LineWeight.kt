package com.github.alekseygett.canvasapp.feature.canvas.domain.models

import androidx.annotation.DimenRes
import com.github.alekseygett.canvasapp.R

enum class LineWeight(@DimenRes val value: Int) {
    THIN(R.dimen.thin_stroke_width),
    MEDIUM(R.dimen.medium_stroke_width),
    THICK(R.dimen.thick_stroke_width),
    EXTRA_THICK(R.dimen.extra_thick_stroke_width);

    companion object {
        private val map = values().associateBy(LineWeight::value)

        fun from(lineWeight: Int) = map[lineWeight] ?: MEDIUM
    }
}