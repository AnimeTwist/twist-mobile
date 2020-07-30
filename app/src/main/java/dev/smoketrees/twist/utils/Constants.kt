package dev.smoketrees.twist.utils

import dev.smoketrees.twist.BuildConfig

object Constants {
    const val PREF = "${BuildConfig.APPLICATION_ID}.pref"

    const val WEB = "https://twist.moe/"

    object PreferenceKeys {
        const val IS_DAY = "is_day"
        const val JWT = "jwt"
    }
}