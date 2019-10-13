package dev.smoketrees.twist.di

import dev.smoketrees.twist.di.module.apiModule
import dev.smoketrees.twist.di.module.cacheModule

val appComponent = listOf(
    apiModule,
    cacheModule
)