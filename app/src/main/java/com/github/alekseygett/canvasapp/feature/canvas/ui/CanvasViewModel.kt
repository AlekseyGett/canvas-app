package com.github.alekseygett.canvasapp.feature.canvas.ui

import com.github.alekseygett.canvasapp.base.BaseViewModel
import com.github.alekseygett.canvasapp.base.Event
import com.github.alekseygett.canvasapp.feature.canvas.domain.models.Color
import com.github.alekseygett.canvasapp.feature.canvas.domain.models.LineWeight
import com.github.alekseygett.canvasapp.feature.canvas.domain.models.ToolsItem

class CanvasViewModel : BaseViewModel<ViewState>() {

    override fun initialViewState() = ViewState(
        colors = Color.values().map { ToolsItem.ColorModel(it.value) },
        lineWeights = LineWeight.values().map { ToolsItem.LineWeightModel(it.value) },
        isColorsToolbarVisible = false,
        isLineWeightsToolbarVisible = false,
        canvasViewState = CanvasViewState(Color.BLACK, LineWeight.MEDIUM)
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnColorsButtonClick -> {
                return previousState.copy(
                    isColorsToolbarVisible = !previousState.isColorsToolbarVisible,
                    isLineWeightsToolbarVisible = false
                )
            }
            is UiEvent.OnLineWeightsButtonClick -> {
                return previousState.copy(
                    isColorsToolbarVisible = false,
                    isLineWeightsToolbarVisible = !previousState.isLineWeightsToolbarVisible
                )
            }
            is UiEvent.OnColorClick -> {
                return previousState.copy(
                    canvasViewState = previousState.canvasViewState.copy(
                        color = Color.from(
                            previousState.colors[event.index].colorRes
                        )
                    ),
                    isColorsToolbarVisible = false
                )
            }
            is UiEvent.OnLineWeightClick -> {
                return previousState.copy(
                    canvasViewState = previousState.canvasViewState.copy(
                        lineWeight = LineWeight.from(
                            previousState.lineWeights[event.index].dimenRes
                        )
                    ),
                    isLineWeightsToolbarVisible = false
                )
            }
        }

        return null
    }

}