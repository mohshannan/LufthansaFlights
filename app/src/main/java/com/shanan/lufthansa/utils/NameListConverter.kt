package com.shanan.lufthansa.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shanan.lufthansa.model.Name

object NameListConverter {

    internal var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<Name> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<Name>>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<Name>): String {
        return gson.toJson(someObjects)
    }
}
