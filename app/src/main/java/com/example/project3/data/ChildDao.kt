package com.example.project3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChildDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun registerChild(child: Child): Long

    @Query("SELECT * FROM Child WHERE email = :email")
    suspend fun getChildByEmail(email: String): Child?

    @Query("SELECT * FROM Child WHERE parent_id = :parentId")
    suspend fun getChildrenByParentId(parentId: Long): List<Child>
}