package com.github.passit.data.datasource.local.model.conversations

import androidx.paging.DataSource
import androidx.room.*
import com.github.passit.data.datasource.local.db.ApplicationDatabase
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ConversationDao constructor(
    private val applicationDatabase: ApplicationDatabase
) {
    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = ConversationLocalData::class)
    abstract suspend fun insert(conversationLocalData: ConversationPartialLocalData)

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = ConversationLocalData::class)
    abstract suspend fun insertAll(conversationLocalData: List<ConversationPartialLocalData>)

    @Transaction
    open suspend fun insertWithAssociated(conversationAndAssociatedLocalData: PartialConversationAndAssociatedLocalData) {
        applicationDatabase.userDao().create(conversationAndAssociatedLocalData.associated)
        insert(conversationAndAssociatedLocalData.conversation)
    }

    @Transaction
    open suspend fun insertAllWithAssociated(conversationAndAssociatedLocalData: List<PartialConversationAndAssociatedLocalData>) {
        applicationDatabase.userDao().insertAll(conversationAndAssociatedLocalData.map { it.associated })
        insertAll(conversationAndAssociatedLocalData.map { it.conversation })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMessage(message: MessageLocalData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllMessages(messages: List<MessageLocalData>)

    @Query("SELECT * FROM conversations")
    abstract fun getConversations(): Flow<ConversationLocalData>

    @Transaction
    @Query("SELECT * FROM conversations ORDER BY created_at DESC")
    abstract fun getConversationsAndAssociated(): DataSource.Factory<Int, ConversationAndAssociatedLocalData>

    @Transaction
    @Query("SELECT * FROM conversations WHERE associated = :userId ORDER BY created_at DESC")
    abstract fun getConversationAndAssociated(userId: String): ConversationAndAssociatedLocalData

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY created_at DESC")
    abstract fun getMessages(conversationId: String): DataSource.Factory<Int, MessageLocalData>

    @Query("UPDATE conversations SET unread_msg = unread_msg + :unread WHERE conversation_id = :conversationId")
    abstract suspend fun incrementUnreadMessagesField(conversationId: String, unread: Int = 1)

    @Query("SELECT COALESCE(sum(COALESCE(unread_msg,0)), 0) FROM conversations")
    abstract suspend fun getUnreadMessagesField(): Int

    @Query("SELECT unread_msg FROM conversations WHERE conversation_id = :conversationId")
    abstract suspend fun getUnreadMessagesField(conversationId: String): Int

    @Query("UPDATE conversations SET unread_msg = 0 WHERE conversation_id = :conversationId")
    abstract suspend fun resetUnreadMessagesField(conversationId: String)

    @Transaction
    @Query("DELETE FROM conversations")
    abstract suspend fun clearConversations()

    @Query("DELETE FROM messages")
    abstract suspend fun clearMessages()
}