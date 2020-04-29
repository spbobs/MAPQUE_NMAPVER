package com.bobs.mapque.nmapver.di

import androidx.room.Room
import com.bobs.mapque.nmapver.searchlist.data.room.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ROOM_DATABASE = "room_database"

val roomModule = module {
    single(named(ROOM_DATABASE)){
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java, "search_list_db"
        ).build()
    }

    single{ (get(named(ROOM_DATABASE)) as AppDatabase).getSearchItemDao()}
}