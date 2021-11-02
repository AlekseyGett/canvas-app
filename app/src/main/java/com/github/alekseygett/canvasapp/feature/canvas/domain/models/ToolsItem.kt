package com.github.alekseygett.canvasapp.feature.canvas.domain.models

import androidx.annotation.ColorRes
import com.github.alekseygett.canvasapp.base.Item

sealed class ToolsItem : Item {
    data class ColorModel(@ColorRes val colorRes: Int) : ToolsItem()
}
