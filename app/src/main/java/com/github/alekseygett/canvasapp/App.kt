package com.github.alekseygett.canvasapp

import android.app.Application
import com.github.alekseygett.canvasapp.feature.canvas.di.canvasModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(canvasModule)
        }
    }
}