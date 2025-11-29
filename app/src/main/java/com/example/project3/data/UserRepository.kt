package com.example.project3.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository(context: Context) {

    private val databaseCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

//            CoroutineScope(Dispatchers.IO).launch {
//                addStarterData()
//            }
        }
    }

    private val database: UserDatabase = Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "users.db"
    )
        .addCallback(databaseCallback)
        .build()

    private val parentDao = database.parentDao()
    private val childDao = database.childDao()

    fun registerParent(parent: Parent) {
        CoroutineScope(Dispatchers.IO).launch {
            parent.id = parentDao.registerParent(parent)
        }
    }
}