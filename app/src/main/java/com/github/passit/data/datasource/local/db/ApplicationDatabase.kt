package com.github.passit.data.datasource.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Transaction
import com.github.passit.data.datasource.local.model.*
import com.github.passit.data.datasource.local.model.conversations.*

@Database(
    entities = [UserLocalData::class, InsertionLocalData::class, InsertionRemoteKeys::class,
               ConversationLocalData::class, ConversationRemoteKeys::class,
               MessageLocalData::class, MessageRemoteKeys::class, UserInsertionsRemoteKeys::class],
    version = 1,
    exportSchema = false)
abstract class ApplicationDatabase: RoomDatabase() {
    abstract fun insertionDao(): InsertionDao
    abstract fun userDao(): UserDao
    abstract fun insertionRemoteKeysDao(): InsertionRemoteKeyDao
    abstract fun conversationDao(): ConversationDao
    abstract fun conversationRemoteKeysDao(): ConversationRemoteKeyDao
    abstract fun messageRemoteKeysDao(): MessageRemoteKeyDao
    abstract fun userInsertionsRemoteKeysDao(): UserInsertionsRemoteKeyDao

    companion object {
        const val DB_NAME = "passit-db"
    }
}