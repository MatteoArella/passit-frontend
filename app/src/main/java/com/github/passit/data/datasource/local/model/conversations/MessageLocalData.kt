package com.github.passit.data.datasource.local.model.conversations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageLocalData(
    @PrimaryKey @ColumnInfo(name = "message_id") var id: String,
    var content: String,
    @ColumnInfo(name = "author_id") var authorId: String,
    @ColumnInfo(name = "conversation_id") var conversationId: String,
    @ColumnInfo(name = "created_at") var createdAt: String,
    @ColumnInfo(name = "updated_at") var updatedAt: String
)