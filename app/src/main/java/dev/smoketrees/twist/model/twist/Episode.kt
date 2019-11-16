package dev.smoketrees.twist.model.twist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

//@Entity(
//    foreignKeys = [
//        ForeignKey(
//            entity = AnimeDetailsEntity::class,
//            parentColumns = arrayOf("anime_id"),
//            childColumns = arrayOf("animeId")
//        )
//    ]
//)

class Episode {

    @SerializedName("anime_id")
    @Expose
    var animeId: Int? = null
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @SerializedName("id")
    @ColumnInfo(name = "ep_id")
    @Expose
    var id: Int? = null
    @SerializedName("number")
    @Expose
    var number: Int? = null
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("animeId", animeId).append("createdAt", createdAt)
            .append("id", id).append("number", number).append("updatedAt", updatedAt).toString()
    }

}