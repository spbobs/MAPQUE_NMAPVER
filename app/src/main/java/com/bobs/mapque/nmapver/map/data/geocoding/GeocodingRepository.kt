package com.bobs.mapque.nmapver.map.data.geocoding

import com.bobs.mapque.nmapver.network.api.NaverApiService
import com.bobs.mapque.nmapver.network.response.GeocodingResponse
import com.bobs.mapque.nmapver.network.response.ReverseGeocodingResponse
import io.reactivex.Single

class GeocodingRepository(private val api: NaverApiService) : GeocodingDataSource {
    override fun searchAddress(query: String): Single<GeocodingResponse> = api.searchAddress(query)

    override fun searchAddressName(coords: String): Single<ReverseGeocodingResponse> = api.searchAddressName(coords, "roadaddr","json")
}