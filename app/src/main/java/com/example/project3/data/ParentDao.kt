package com.example.project3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface ParentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun registerParent(parent: Parent): Long
}