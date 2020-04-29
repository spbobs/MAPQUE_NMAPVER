package com.bobs.mapque.nmapver

import com.bobs.baselibrary.base.BaseApplication
import com.bobs.mapque.nmapver.di.*
import com.bobs.mapque.nmapver.util.ADManager
import com.google.android.gms.ads.MobileAds
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : BaseApplication(){
    override fun onCreate() {
        super.onCreate()

        // koin
        startKoin {
            androidContext(this@MyApplication)
            modules(vmModule, apiModule, dataModule, roomModule, prefsModule)
        }

        // debug 일때만 로그 보기
        Logger.addLogAdapter(object: AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return isDebuggable()
            }
        })

        // admob 초기화
        MobileAds.initialize(this) {}

        // 광고 미리 로드
        ADManager.loadAd(this)
    }
}