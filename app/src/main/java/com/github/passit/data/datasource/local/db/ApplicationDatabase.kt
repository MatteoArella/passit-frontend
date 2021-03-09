package com.github.passit.data.datasource.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.passit.data.datasource.local.model.*

@Database(
    entities = [UserLocalData::class, InsertionLocalData::class, InsertionRemoteKeys::class],
    version = 1,
    exportSchema = false)
abstract class ApplicationDatabase: RoomDatabase() {
    abstract fun insertionDao(): InsertionDao
    abstract fun userDao(): UserDao
    abstract fun insertionRemoteKeysDao(): InsertionRemoteKeyDao

    companion object {
        const val DB_NAME = "passit-db"
    }
}