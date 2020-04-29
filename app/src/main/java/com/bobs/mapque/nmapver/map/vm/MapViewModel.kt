package com.bobs.mapque.nmapver.map.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bobs.baselibrary.base.BaseViewModel
import com.bobs.mapque.nmapver.map.data.ResultListener
import com.bobs.mapque.nmapver.map.data.geocoding.GeocodingDataSource
import com.bobs.mapque.nmapver.network.response.GeocodingResponse
import com.bobs.mapque.nmapver.searchlist.data.model.SearchItem
import com.bobs.mapque.nmapver.searchlist.data.source.SearchListDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime

class MapViewModel(
    private val geocodingDataSource: GeocodingDataSource,
    private val searchListDataSource: SearchListDataSource
) : BaseViewModel(){

    private val _searchAddressData: MutableLiveData<GeocodingResponse> = MutableLiveData()
    val searchAddressData: LiveData<GeocodingResponse> = _searchAddressData

    fun getSearchAddress(query: String, listener: ResultListener<String>){
        addDisposable(
            geocodingDataSource.searchAddress(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.run {
                        if(meta?.totalCount!! > 0){
                            _searchAddressData.value = this
                            listener.success("")
                        } else {
                            listener.fail()
                        }
                    }
                },{
                    listener.fail(it.message)
                })
        )
    }

    fun getSearchAddressName(coords: String, listener: ResultListener<String?>){
        addDisposable(
            geocodingDataSource.searchAddressName(coords)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.run {
                        if(results?.size!! > 0){
                            listener.success(results[0]?.convertAddress())
                        } else {
                            listener.fail()
                        }
                    }
                },{
                    listener.fail(it.message)
                })
        )
    }

    fun insertSearchAddress(query: String, address: String, latitude: Double, longitude: Double, date: DateTime){
        val serachItem = SearchItem(0, query, address, latitude, longitude, date)
        searchListDataSource.insertSearchItem(serachItem)
    }
}