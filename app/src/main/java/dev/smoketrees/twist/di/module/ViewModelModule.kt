package dev.smoketrees.twist.di.module

import dev.smoketrees.twist.ui.AnimeViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AnimeViewModel(get()) }
}