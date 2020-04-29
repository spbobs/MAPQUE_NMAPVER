package com.bobs.mapque.nmapver.di

import com.bobs.mapque.nmapver.BuildConfig
import com.bobs.mapque.nmapver.network.api.NaverApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = module {
    single<NaverApiService> {
        Retrofit.Builder()
            .baseUrl(BuildConfig.NAVER_API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create(NaverApiService::class.java)
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .addInterceptor { chain ->
                val requestBuilder =  chain.request().newBuilder()
                requestBuilder.addHeader("X-NCP-APIGW-API-KEY-ID",BuildConfig.NAVER_CLIENT_ID)
                    .addHeader("X-NCP-APIGW-API-KEY",BuildConfig.NAVER_CLIENT_SECRET)

                chain.proceed(requestBuilder.build())
            }
            .build()
    }
}