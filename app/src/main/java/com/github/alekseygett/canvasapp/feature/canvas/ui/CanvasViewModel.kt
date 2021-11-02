package com.github.alekseygett.canvasapp.feature.canvas.ui

import com.github.alekseygett.canvasapp.base.BaseViewModel
import com.github.alekseygett.canvasapp.base.Event
import com.github.alekseygett.canvasapp.feature.canvas.domain.models.COLOR
import com.github.alekseygett.canvasapp.feature.canvas.domain.models.ToolsItem

class CanvasViewModel : BaseViewModel<ViewState>() {

    override fun initialViewState() = ViewState(
        colors = COLOR.values().map { ToolsItem.ColorModel(it.value) },
        isPaletteVisible = false,
        canvasViewState = CanvasViewState(COLOR.BLACK)
    )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnToolsClick -> {
                return previousState.copy(isPaletteVisible = !previousState.isPaletteVisible)
            }
            is UiEvent.OnColorClick -> {
                return previousState.copy(
                    canvasViewState = previousState.canvasViewState.copy(
                        color = COLOR.from(
                            previousState.colors[event.index].colorRes
                        )
                    ),
                    isPaletteVisible = !previousState.isPaletteVisible
                )
            }
        }

        return null
    }

}