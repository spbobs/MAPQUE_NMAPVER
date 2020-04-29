package com.bobs.mapque.nmapver.di

import com.bobs.mapque.nmapver.map.data.geocoding.GeocodingDataSource
import com.bobs.mapque.nmapver.map.data.geocoding.GeocodingRepository
import com.bobs.mapque.nmapver.searchlist.data.source.SearchListDataSource
import com.bobs.mapque.nmapver.searchlist.data.source.SearchListRepository
import org.koin.dsl.module

val dataModule = module {
    single<GeocodingDataSource> { GeocodingRepository(get())}
    single<SearchListDataSource> { SearchListRepository(get())}
}