package com.github.alekseygett.canvasapp.feature.canvas.ui

import com.github.alekseygett.canvasapp.feature.canvas.domain.models.ToolsItem
import com.github.alekseygett.canvasapp.base.Event
import com.github.alekseygett.canvasapp.feature.canvas.domain.models.Color
import com.github.alekseygett.canvasapp.feature.canvas.domain.models.LineWeight

data class ViewState(
    val colors: List<ToolsItem.ColorModel>,
    val lineWeights: List<ToolsItem.LineWeightModel>,
    val isColorsToolbarVisible: Boolean,
    val isLineWeightsToolbarVisible: Boolean,
    val canvasViewState: CanvasViewState
)

data class CanvasViewState(
    val color: Color,
    val lineWeight: LineWeight
)

sealed class UiEvent : Event {
    object OnColorsButtonClick : UiEvent()
    object OnLineWeightsButtonClick : UiEvent()
    data class OnColorClick(val index: Int) : UiEvent()
    data class OnLineWeightClick(val index: Int) : UiEvent()
}