package com.github.alekseygett.canvasapp.feature.canvas.domain.models

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.github.alekseygett.canvasapp.base.Item

sealed class ToolsItem : Item {
    data class ColorModel(@ColorRes val colorRes: Int) : ToolsItem()
    data class LineWeightModel(@DimenRes val dimenRes: Int) : ToolsItem()
}
