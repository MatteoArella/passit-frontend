package com.github.passit.data.datasource.local.model.conversations

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConversationRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(conversationRemoteKeys: List<ConversationRemoteKeys>)

    @Query("SELECT * FROM conversation_remote_keys WHERE conversation_id = :conversationId")
    suspend fun getRemoteKeyByConversationId(conversationId: String): ConversationRemoteKeys?

    @Query("DELETE FROM conversation_remote_keys")
    suspend fun cleanRemoteKeys()
}