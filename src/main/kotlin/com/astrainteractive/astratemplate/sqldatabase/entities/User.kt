package com.astrainteractive.astratemplate.sqldatabase.entities


import com.astrainteractive.astratemplate.sqldatabase.lib.ColumnInfo
import com.astrainteractive.astratemplate.sqldatabase.lib.Entity
import com.astrainteractive.astratemplate.sqldatabase.lib.PrimaryKey

@Entity(User.TABLE)
data class User(
    @PrimaryKey(autoIncrement = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "discord_id")
    val discordId: String,
    @ColumnInfo(name = "minecraft_uuid")
    val minecraftUuid: String,
) {
    companion object {
        const val TABLE: String = "users"
    }
}

@Entity(RatingRelation.TABLE)
data class RatingRelation(
    @PrimaryKey(autoIncrement = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "user_id")
    val userID: Long,
    @ColumnInfo(name = "reason")
    val reason: String,
) {
    companion object {
        const val TABLE: String = "rating"
    }
}


