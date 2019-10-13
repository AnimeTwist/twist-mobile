package dev.smoketrees.twist.di.module

import dev.smoketrees.twist.repository.AnimeRepo
import org.koin.dsl.module

val repoModule = module {
    factory { AnimeRepo(get(), get()) }
}