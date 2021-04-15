package com.github.passit.data.datasource.local.model.conversations

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messageRemoteKeys: List<MessageRemoteKeys>)

    @Query("SELECT * FROM message_remote_keys WHERE message_id = :messageId")
    suspend fun getRemoteKeyByMessageId(messageId: String): MessageRemoteKeys?

    @Query("DELETE FROM message_remote_keys")
    suspend fun cleanRemoteKeys()
}