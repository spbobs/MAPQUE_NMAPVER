package com.bobs.mapque.nmapver.searchlist.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bobs.mapque.nmapver.searchlist.data.model.SearchItem

@Dao
interface SearchItemDao {
    @Query("select * from search_items order by search_date desc")
    fun getAllSearchItems() : List<SearchItem>

    @Insert
    fun insertSearchItem(searchItem: SearchItem)

    @Delete
    fun deleteSearchItem(searchItem: SearchItem)
}