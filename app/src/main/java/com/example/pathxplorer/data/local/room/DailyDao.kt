package com.example.pathxplorer.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pathxplorer.data.local.entity.DailyQuestEntity

@Dao
interface DailyDao {

    @Query("SELECT * FROM daily_quest WHERE id_user = :idUser")
    suspend fun getDailyQuest(idUser: Int): DailyQuestEntity

    @Query("UPDATE daily_quest SET daily_quest_count = daily_quest_count + 1 WHERE id_user = :idUser")
    suspend fun updateDailyQuestCount(idUser: Int)

    @Query("UPDATE daily_quest SET daily_quest_score = daily_quest_score + :score WHERE id_user = :idUser")
    suspend fun updateScore(idUser: Int, score: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyQuest(dailyQuestEntity: DailyQuestEntity)

    @Query("SELECT COUNT(*) FROM daily_quest WHERE id_user = :idUser")
    suspend fun checkDailyQuest(idUser: Int): Int

}