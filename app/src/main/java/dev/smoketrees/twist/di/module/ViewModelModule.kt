package dev.smoketrees.twist.di.module

import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.ui.player.EpisodesViewModel
import dev.smoketrees.twist.ui.player.PlayerViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AnimeViewModel(get()) }
    viewModel { EpisodesViewModel(get()) }
    viewModel { PlayerViewModel(get()) }
}