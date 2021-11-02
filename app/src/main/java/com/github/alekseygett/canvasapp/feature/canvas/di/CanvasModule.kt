package com.github.alekseygett.canvasapp.feature.canvas.di

import com.github.alekseygett.canvasapp.feature.canvas.ui.CanvasViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val canvasModule = module {
    viewModel<CanvasViewModel> {
        CanvasViewModel()
    }
}