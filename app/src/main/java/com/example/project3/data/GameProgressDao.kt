package com.example.project3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: GameProgress): Long

    @Query("SELECT * FROM GameProgress WHERE child_id = :childId ORDER BY timestamp DESC")
    suspend fun getProgressByChildId(childId: Long): List<GameProgress>

    @Query("SELECT * FROM GameProgress WHERE child_id = :childId AND level = :level AND gameNumber = :gameNumber ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestGameProgress(childId: Long, level: Int, gameNumber: Int): GameProgress?

    @Query("SELECT COUNT(*) FROM GameProgress WHERE child_id = :childId AND completed = 1")
    suspend fun getTotalCompletedGames(childId: Long): Int

    @Query("SELECT SUM(score) FROM GameProgress WHERE child_id = :childId")
    suspend fun getTotalScore(childId: Long): Int?

    @Query("SELECT AVG(score) FROM GameProgress WHERE child_id = :childId")
    suspend fun getAverageScore(childId: Long): Double?
}