package com.example.pathxplorer.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "daily_quest")
class DailyQuestEntity(
    @ColumnInfo(name = "id_user")
    @PrimaryKey
    val idUser: Int,

    @ColumnInfo(name = "email_user")
    val emailUser: String,

    @ColumnInfo(name = "last_check")
    val lastCheck: String,

    @ColumnInfo(name = "daily_quest_count")
    val dailyQuestCount: Int,

    @ColumnInfo(name = "daily_quest_score")
    val score: Int
)