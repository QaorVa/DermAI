package com.example.dermai.utils

import androidx.room.TypeConverter
import com.example.dermai.data.model.Skincare
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromSkincare(skincare: Skincare): String {
        return Gson().toJson(skincare)
    }

    @TypeConverter
    fun toSkincare(skincareString: String): Skincare {
        return Gson().fromJson(skincareString, Skincare::class.java)
    }
}