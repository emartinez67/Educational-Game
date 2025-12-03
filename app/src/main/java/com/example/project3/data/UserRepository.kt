package com.example.project3.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRepository(context: Context) {

    private val databaseCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
        }
    }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS GameProgress (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    child_id INTEGER NOT NULL,
                    level INTEGER NOT NULL,
                    gameNumber INTEGER NOT NULL,
                    score INTEGER NOT NULL,
                    completed INTEGER NOT NULL,
                    attempts INTEGER NOT NULL,
                    timestamp INTEGER NOT NULL,
                    FOREIGN KEY(child_id) REFERENCES Child(id) ON DELETE CASCADE
                )
            """)
        }
    }

    private val database: UserDatabase = Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "users.db"
    )
        .addCallback(databaseCallback)
        .addMigrations(MIGRATION_1_2)
        .build()

    private val parentDao = database.parentDao()
    private val childDao = database.childDao()
    private val gameProgressDao = database.gameProgressDao()

    fun registerParent(parent: Parent) {
        CoroutineScope(Dispatchers.IO).launch {
            parent.id = parentDao.registerParent(parent)
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

    fun registerChild(child: Child) {
        CoroutineScope(Dispatchers.IO).launch {
            child.id = childDao.registerChild(child)
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

    suspend fun deleteChild(child: Child) {
        withContext(Dispatchers.IO) {
            childDao.deleteChild(child)
        }
    }

    suspend fun saveGameProgress(progress: GameProgress) {
        withContext(Dispatchers.IO) {
            gameProgressDao.insertProgress(progress)
        }
    }

    suspend fun getChildProgress(childId: Long): List<GameProgress> {
        return withContext(Dispatchers.IO) {
            gameProgressDao.getProgressByChildId(childId)
        }
    }

    suspend fun getProgressForLevel(childId: Long, level: Int): List<GameProgress> {
        return withContext(Dispatchers.IO) {
            gameProgressDao.getProgressByChildId(childId).filter { it.level == level }
        }
    }

    suspend fun getLatestGameProgress(childId: Long, level: Int, gameNumber: Int): GameProgress? {
        return withContext(Dispatchers.IO) {
            gameProgressDao.getLatestGameProgress(childId, level, gameNumber)
        }
    }

    suspend fun getTotalCompletedGames(childId: Long): Int {
        return withContext(Dispatchers.IO) {
            gameProgressDao.getTotalCompletedGames(childId)
        }
    }

    suspend fun getTotalScore(childId: Long): Int? {
        return withContext(Dispatchers.IO) {
            gameProgressDao.getTotalScore(childId)
        }
    }

    suspend fun getAverageScore(childId: Long): Double? {
        return withContext(Dispatchers.IO) {
            gameProgressDao.getAverageScore(childId)
        }
    }
}