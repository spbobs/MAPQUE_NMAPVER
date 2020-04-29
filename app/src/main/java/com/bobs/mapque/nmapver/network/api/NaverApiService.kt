package com.bobs.mapque.nmapver.network.api

import com.bobs.mapque.nmapver.network.response.GeocodingResponse
import com.bobs.mapque.nmapver.network.response.ReverseGeocodingResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverApiService {
    // Geocoding
    @GET("/map-geocode/v2/geocode")
    fun searchAddress(
        @Query("query") query: String
    ) : Single<GeocodingResponse>

    // Reverse Geocoding
    @GET("/map-reversegeocode/v2/gc")
    fun searchAddressName(
        @Query("coords") coords: String,
        @Query("orders") orders: String,
        @Query("output") output: String
    ) : Single<ReverseGeocodingResponse>
}