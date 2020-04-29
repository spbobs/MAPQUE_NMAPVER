package com.bobs.mapque.nmapver.map.data

interface ResultListener<T> {
    fun success(result: T)
    fun fail(msg: String? = null)
}