package com.bobs.mapque.nmapver.network.response

interface IResult<T> {
    fun success(result: T)
    fun fail(msg: String? = null)
}