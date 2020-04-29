package com.bobs.mapque.nmapver.util.listener

interface SearchListListener<T> {
    fun shareData(item: T)
    fun deleteItem(item: T)
}