package dev.smoketrees.twist

import android.app.Application
import androidx.multidex.MultiDexApplication
import dev.smoketrees.twist.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appComponent)
        }
    }
}