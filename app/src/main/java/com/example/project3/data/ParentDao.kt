package com.example.project3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ParentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun registerParent(parent: Parent): Long

    @Query("SELECT * FROM Parent WHERE email = :email")
    suspend fun getParentByEmail(email: String): Parent?
}