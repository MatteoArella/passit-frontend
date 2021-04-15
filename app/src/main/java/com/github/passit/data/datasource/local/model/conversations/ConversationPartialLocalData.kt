package com.github.passit.data.datasource.local.model.conversations

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

//@Entity(tableName = ConversationLocalData.CONVERSATION_TABLE_NAME)
data class ConversationPartialLocalData(
        @PrimaryKey @ColumnInfo(name = ConversationLocalData.CONVERSATION_ID_COL_NAME) var conversationId: String,
        @ColumnInfo(name = ConversationLocalData.ASSOCIATED_COL_NAME) var associated: String,
        @ColumnInfo(name = ConversationLocalData.CREATED_AT_COL_NAME) var createdAt: String,
        @ColumnInfo(name = ConversationLocalData.UPDATED_AT_COL_NAME) var updatedAt: String
)