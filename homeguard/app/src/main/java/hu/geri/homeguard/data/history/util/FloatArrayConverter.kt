package hu.geri.homeguard.data.history.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FloatArrayConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromFloatArrayArray(value: String): Array<FloatArray> {
        val arrayType = object : TypeToken<Array<FloatArray>>() {}.type
        return gson.fromJson(value, arrayType)
    }

    @TypeConverter
    fun toFloatArrayArray(value: Array<FloatArray>): String {
        return gson.toJson(value)
    }
}