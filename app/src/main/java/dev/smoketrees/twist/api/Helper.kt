package dev.smoketrees.twist.api

import android.content.Context
import dev.smoketrees.twist.BuildConfig
import dev.smoketrees.twist.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun getOkHttpClient(context: Context): OkHttpClient {

    val httpClient = OkHttpClient.Builder()

    httpClient.addInterceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader(
                "x-access-token",
                context.getString(R.string.access_token)
            )
        val request = requestBuilder.build()
        return@addInterceptor chain.proceed(request)
    }

    if (BuildConfig.DEBUG) {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpClient.addInterceptor(
            httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        )
    }

    return httpClient.build()
}