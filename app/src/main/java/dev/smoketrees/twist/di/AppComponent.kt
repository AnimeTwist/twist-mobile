package dev.smoketrees.twist.di

import dev.smoketrees.twist.di.module.*

val appComponent = listOf(
    apiModule,
    cacheModule,
    repoModule,
    viewModelModule,
    roomModule
)