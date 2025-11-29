package com.example.project3.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun registerChild(child: Child) {
        CoroutineScope(Dispatchers.IO).launch {
            child.id = childDao.registerChild(child)
        }
    }

    suspend fun authenticateParent(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val parent = parentDao.getParentByEmail(email)
            parent != null && parent.password == password
        }
    }

    suspend fun getParentByEmail(email: String): Parent? {
        return withContext(Dispatchers.IO) {
            parentDao.getParentByEmail(email)
        }
    }

    suspend fun authenticateChild(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val child = childDao.getChildByEmail(email)
            child != null && child.password == password
        }
    }

    suspend fun getChildByEmail(email: String): Child? {
        return withContext(Dispatchers.IO) {
            childDao.getChildByEmail(email)
        }
    }

    suspend fun getChildrenByParentId(parentId: Long): List<Child> {
        return withContext(Dispatchers.IO) {
            childDao.getChildrenByParentId(parentId)
        }
    }
}