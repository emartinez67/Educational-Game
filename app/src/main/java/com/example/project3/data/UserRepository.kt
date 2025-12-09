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

    /**
     * Database migration to add GameProgress table that was added after initial creation
     */
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

    /**
     * Registers a parent into the database
     */
    fun registerParent(parent: Parent) {
        CoroutineScope(Dispatchers.IO).launch {
            parent.id = parentDao.registerParent(parent)
        }
    }

    /**
     * Authenticates a parent based off the email and password given
     */
    suspend fun authenticateParent(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val parent = parentDao.getParentByEmail(email)
            parent != null && parent.password == password
        }
    }

    /**
     * Gets a parent based off the email given
     */
    suspend fun getParentByEmail(email: String): Parent? {
        return withContext(Dispatchers.IO) {
            parentDao.getParentByEmail(email)
        }
    }

    /**
     * Registers a child into the database
     */
    fun registerChild(child: Child) {
        CoroutineScope(Dispatchers.IO).launch {
            child.id = childDao.registerChild(child)
        }
    }

    /**
     * Authenticates a child based on the email and password given
     */
    suspend fun authenticateChild(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val child = childDao.getChildByEmail(email)
            child != null && child.password == password
        }
    }

    /**
     * Gets a child based off the email given
     */
    suspend fun getChildByEmail(email: String): Child? {
        return withContext(Dispatchers.IO) {
            childDao.getChildByEmail(email)
        }
    }

    /**
     * Gets a child based of a parent's ID
     */
    suspend fun getChildrenByParentId(parentId: Long): List<Child> {
        return withContext(Dispatchers.IO) {
            childDao.getChildrenByParentId(parentId)
        }
    }

    /**
     * Deletes a child from the database
     */
    suspend fun deleteChild(child: Child) {
        withContext(Dispatchers.IO) {
            childDao.deleteChild(child)
        }
    }

    /**
     * Saves game progress to the database
     */
    suspend fun saveGameProgress(progress: GameProgress) {
        withContext(Dispatchers.IO) {
            gameProgressDao.insertProgress(progress)
        }
    }

    /**
     * Gets a child's progress from the database
     */
    suspend fun getChildProgress(childId: Long): List<GameProgress> {
        return withContext(Dispatchers.IO) {
            gameProgressDao.getProgressByChildId(childId)
        }
    }

    /**
     * Get's a child's progress for a certain level
     */
    suspend fun getProgressForLevel(childId: Long, level: Int): List<GameProgress> {
        return withContext(Dispatchers.IO) {
            gameProgressDao.getProgressByChildId(childId).filter { it.level == level }
        }
    }

    /**
     * Gets the total number of completed games
     */
    suspend fun getTotalCompletedGames(childId: Long): Int {
        return withContext(Dispatchers.IO) {
            gameProgressDao.getTotalCompletedGames(childId)
        }
    }

    /**
     * Gets a child's total score
     */
    suspend fun getTotalScore(childId: Long): Int? {
        return withContext(Dispatchers.IO) {
            gameProgressDao.getTotalScore(childId)
        }
    }

    /**
     * Get's a child's average score
     */
    suspend fun getAverageScore(childId: Long): Double? {
        return withContext(Dispatchers.IO) {
            gameProgressDao.getAverageScore(childId)
        }
    }
}