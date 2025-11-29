package com.example.project3.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(entity = Parent::class,
        parentColumns = ["id"],
        childColumns = ["parent_id"],
        onDelete = ForeignKey.CASCADE)
])
data class Child (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var firstName: String,

    var lastName: String,

    var email: String,

    var password: String,

    @ColumnInfo(name = "parent_id")
    var parentId: Long,

    @ColumnInfo(name = "created")
    var creationTime: Long = System.currentTimeMillis()
)