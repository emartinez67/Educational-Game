package com.example.project3.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Parent::class, Child::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun parentDao(): ParentDao
    abstract fun childDao(): ChildDao
}