package com.github.passit.data.datasource.local.model.conversations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversation_remote_keys")
data class ConversationRemoteKeys(
    @PrimaryKey @ColumnInfo(name = "conversation_id") val conversationId: String,
    @ColumnInfo(name = "next_key") val nextKey: String?
)