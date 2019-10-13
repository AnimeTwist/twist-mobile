package dev.smoketrees.twist.di.module

import dev.smoketrees.twist.utils.Constants
import dev.smoketrees.twist.utils.PreferenceHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val cacheModule = module {
    single { PreferenceHelper.customPrefs(androidContext(), Constants.PREF) }
}