package com.example.project3.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Parent (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var firstName: String,

    var lastName: String,

    var email: String,

    var password: String,

    @ColumnInfo(name = "created")
    var creationTime: Long = System.currentTimeMillis()
)