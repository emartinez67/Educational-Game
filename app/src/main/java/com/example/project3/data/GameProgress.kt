package com.example.project3.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(entity = Child::class,
        parentColumns = ["id"],
        childColumns = ["child_id"],
        onDelete = ForeignKey.CASCADE)
])
data class GameProgress (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "child_id")
    var childId: Long,

    var level: Int,

    var gameNumber: Int,

    var score: Int,

    var completed: Boolean,

    var attempts: Int,

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = System.currentTimeMillis()
)