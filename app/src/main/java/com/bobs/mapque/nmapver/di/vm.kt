package com.bobs.mapque.nmapver.di

import com.bobs.mapque.nmapver.map.vm.MapViewModel
import com.bobs.mapque.nmapver.searchlist.vm.SearchListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val vmModule = module {
    viewModel { MapViewModel(get(),get()) }
    viewModel { SearchListViewModel(get()) }
}