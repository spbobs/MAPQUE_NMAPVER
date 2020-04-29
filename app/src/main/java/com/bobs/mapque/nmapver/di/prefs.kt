package com.bobs.mapque.nmapver.di

import com.bobs.mapque.nmapver.util.ext.sharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val prefsModule = module {
    single{ androidApplication().sharedPreferences()}
}