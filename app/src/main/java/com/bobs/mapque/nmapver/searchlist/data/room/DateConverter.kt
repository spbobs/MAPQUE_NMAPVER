package com.bobs.mapque.nmapver.searchlist.data.room

import androidx.room.TypeConverter
import org.joda.time.DateTime

class DateConverter {
    @TypeConverter
    fun longToDatetime(time: Long) = DateTime(time)

    @TypeConverter
    fun datetimeToLong(dateTime: DateTime) = dateTime.millis
}