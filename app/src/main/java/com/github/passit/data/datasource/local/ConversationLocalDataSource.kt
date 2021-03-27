package com.github.passit.data.datasource.local

import androidx.paging.DataSource
import androidx.room.withTransaction
import com.github.passit.data.datasource.local.db.ApplicationDatabase
import com.github.passit.data.datasource.local.model.conversations.*
import javax.inject.Inject

class ConversationLocalDataSource @Inject constructor(
    private val applicationDatabase: ApplicationDatabase
) {
    suspend fun <R> withTransaction(block: suspend () -> R) = applicationDatabase.withTransaction(block)

    suspend fun createConversation(conversationLocalData: ConversationPartialLocalData)
        = applicationDatabase.conversationDao().insert(conversationLocalData)

    suspend fun createConversationWithAssociated(conversationAndAssociatedLocalData: PartialConversationAndAssociatedLocalData)
            = applicationDatabase.conversationDao().insertWithAssociated(conversationAndAssociatedLocalData)

    suspend fun createMessage(messageLocalData: MessageLocalData)
        = applicationDatabase.conversationDao().insertMessage(messageLocalData)

    suspend fun insertAllWithAssociated(conversationAndAssociatedLocalData: List<PartialConversationAndAssociatedLocalData>)
            = applicationDatabase.conversationDao().insertAllWithAssociated(conversationAndAssociatedLocalData)

    suspend fun insertAllMessages(messages: List<MessageLocalData>)
            = applicationDatabase.conversationDao().insertAllMessages(messages)

    fun getConversationsWithAssociated(): DataSource.Factory<Int, ConversationAndAssociatedLocalData>
        = applicationDatabase.conversationDao().getConversationsAndAssociated()

    fun getMessages(conversationId: String): DataSource.Factory<Int, MessageLocalData>
        = applicationDatabase.conversationDao().getMessages(conversationId)

    suspend fun insertAllConversationRemoteKeys(conversationRemoteKeys: List<ConversationRemoteKeys>) =
            applicationDatabase.conversationRemoteKeysDao().insertAll(conversationRemoteKeys)

    suspend fun getRemoteKeyByConversationId(conversationId: String): ConversationRemoteKeys? =
            applicationDatabase.conversationRemoteKeysDao().getRemoteKeyByConversationId(conversationId)

    suspend fun cleanConversationRemoteKeys()
        = applicationDatabase.conversationRemoteKeysDao().cleanRemoteKeys()

    suspend fun insertAllMessageRemoteKeys(messageRemoteKeys: List<MessageRemoteKeys>) =
            applicationDatabase.messageRemoteKeysDao().insertAll(messageRemoteKeys)

    suspend fun getRemoteKeyByMessageId(messageId: String): MessageRemoteKeys? =
            applicationDatabase.messageRemoteKeysDao().getRemoteKeyByMessageId(messageId)

    suspend fun incrementUnreadMessages(conversationId: String, unread: Int = 1) =
            applicationDatabase.conversationDao().incrementUnreadMessagesField(conversationId, unread)

    suspend fun getUnreadMessagesCount(conversationId: String?): Int {
        return if (conversationId == null)
            applicationDatabase.conversationDao().getUnreadMessagesField()
        else
            applicationDatabase.conversationDao().getUnreadMessagesField(conversationId)
    }

    suspend fun resetUnreadMessages(conversationId: String) =
            applicationDatabase.conversationDao().resetUnreadMessagesField(conversationId)

    suspend fun cleanMessageRemoteKeys()
            = applicationDatabase.messageRemoteKeysDao().cleanRemoteKeys()

    suspend fun clearConversations() = applicationDatabase.conversationDao().clearConversations()

    suspend fun clearMessages() = applicationDatabase.conversationDao().clearMessages()
}