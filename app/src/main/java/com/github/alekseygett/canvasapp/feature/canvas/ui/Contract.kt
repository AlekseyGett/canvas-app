package com.github.alekseygett.canvasapp.feature.canvas.ui

import com.github.alekseygett.canvasapp.feature.canvas.domain.models.ToolsItem
import com.github.alekseygett.canvasapp.base.Event
import com.github.alekseygett.canvasapp.feature.canvas.domain.models.COLOR

data class ViewState(
    val colors: List<ToolsItem.ColorModel>,
    val isPaletteVisible: Boolean,
    val canvasViewState: CanvasViewState
)

data class CanvasViewState(
    val color: COLOR
)

sealed class UiEvent : Event {
    object OnToolsClick : UiEvent()
    data class OnColorClick(val index: Int) : UiEvent()
}