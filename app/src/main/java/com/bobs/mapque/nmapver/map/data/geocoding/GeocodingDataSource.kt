package com.bobs.mapque.nmapver.map.data.geocoding

import com.bobs.mapque.nmapver.network.response.GeocodingResponse
import com.bobs.mapque.nmapver.network.response.ReverseGeocodingResponse
import io.reactivex.Single

interface GeocodingDataSource {
    fun searchAddress(query: String): Single<GeocodingResponse>

    fun searchAddressName(coords: String): Single<ReverseGeocodingResponse>
}