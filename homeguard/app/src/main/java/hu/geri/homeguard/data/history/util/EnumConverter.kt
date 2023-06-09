package hu.geri.homeguard.data.history.util

import androidx.room.TypeConverter
import hu.geri.homeguard.data.history.model.HistoryEnum

class EnumConverter {

    @TypeConverter
    fun toEnum(value: String): HistoryEnum {
        return enumValueOf(value)
    }

    @TypeConverter
    fun toString(value: HistoryEnum): String {
        return value.name
    }
}