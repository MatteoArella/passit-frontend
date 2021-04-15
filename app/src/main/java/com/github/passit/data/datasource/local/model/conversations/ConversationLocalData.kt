package com.github.passit.data.datasource.local.model.conversations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ConversationLocalData.CONVERSATION_TABLE_NAME)
data class ConversationLocalData(
        @PrimaryKey @ColumnInfo(name = CONVERSATION_ID_COL_NAME) var conversationId: String,
        @ColumnInfo(name = ASSOCIATED_COL_NAME) var associated: String,
        @ColumnInfo(name = UNREAD_MSG_COL_NAME, defaultValue = "0") var unreadMsg: Int,
        @ColumnInfo(name = CREATED_AT_COL_NAME) var createdAt: String,
        @ColumnInfo(name = UPDATED_AT_COL_NAME) var updatedAt: String
) {
    companion object {
        const val CONVERSATION_TABLE_NAME = "conversations"
        const val CONVERSATION_ID_COL_NAME = "conversation_id"
        const val ASSOCIATED_COL_NAME = "associated"
        const val UNREAD_MSG_COL_NAME = "unread_msg"
        const val CREATED_AT_COL_NAME = "created_at"
        const val UPDATED_AT_COL_NAME = "updated_at"
    }
}