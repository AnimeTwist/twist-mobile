package dev.smoketrees.twist.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.smoketrees.twist.model.twist.Episode
import java.util.Collections.emptyList


class EpisodeListTypeConverter {
    val gson = Gson()

    @TypeConverter
    fun stringToEpisodeList(data: String?): List<Episode> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<Episode>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun episodeListToString(someObjects: List<Episode>): String {
        return gson.toJson(someObjects)
    }
}