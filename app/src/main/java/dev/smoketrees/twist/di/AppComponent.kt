package dev.smoketrees.twist.di

import dev.smoketrees.twist.di.module.apiModule
import dev.smoketrees.twist.di.module.cacheModule
import dev.smoketrees.twist.di.module.repoModule
import dev.smoketrees.twist.di.module.viewModelModule

val appComponent = listOf(
    apiModule,
    cacheModule,
    repoModule,
    viewModelModule
)