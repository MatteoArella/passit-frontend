package com.github.passit.data.datasource.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.passit.data.datasource.local.model.InsertionLocalData
import com.github.passit.data.datasource.local.model.InsertionDao
import com.github.passit.data.datasource.local.model.UserDao
import com.github.passit.data.datasource.local.model.UserLocalData

@Database(entities = [UserLocalData::class, InsertionLocalData::class], version = 1)
abstract class ApplicationDatabase: RoomDatabase() {
    abstract fun insertionDao(): InsertionDao
    abstract fun userDao(): UserDao

    companion object {
        const val DB_NAME = "passit-db"
    }
}