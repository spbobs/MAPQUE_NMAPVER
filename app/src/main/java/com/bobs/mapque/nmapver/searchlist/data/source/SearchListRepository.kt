package com.bobs.mapque.nmapver.searchlist.data.source

import com.bobs.mapque.nmapver.searchlist.data.model.SearchItem
import com.bobs.mapque.nmapver.searchlist.data.room.SearchItemDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchListRepository(private val dao: SearchItemDao) : SearchListDataSource{
    override fun getAllSearchItems(): List<SearchItem> = dao.getAllSearchItems()

    override fun insertSearchItem(searchItem: SearchItem) {
        GlobalScope.launch {
            dao.insertSearchItem(searchItem)
        }
    }

    override fun deleteSearchItem(searchItem: SearchItem) {
        GlobalScope.launch {
            dao.deleteSearchItem(searchItem)
        }
    }
}