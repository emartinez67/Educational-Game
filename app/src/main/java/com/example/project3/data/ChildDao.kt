package com.example.project3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface ChildDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun registerChild(child: Child): Long
}