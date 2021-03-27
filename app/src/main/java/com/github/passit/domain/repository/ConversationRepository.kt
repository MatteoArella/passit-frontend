package com.github.passit.domain.repository

import androidx.paging.PagingData
import com.github.passit.domain.model.BaseMessage
import com.github.passit.domain.model.Conversation
import com.github.passit.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun createConversation(userId: String, tutorId: String): Flow<Conversation>
    fun getConversationPages(userId: String): Flow<PagingData<Conversation>>
    fun createMessage(to: String, conversationId: String, content: String): Flow<BaseMessage>
    fun getMessages(conversationId: String): Flow<PagingData<BaseMessage>>
    fun getUnreadMessagesCount(conversationId: String?): Flow<Int>
    fun resetUnreadMessagesCount(conversationId: String): Flow<Unit>

    fun subscribeConversations(userId: String): Flow<Conversation>
    fun subscribeMessages(userId: String): Flow<Message.ReceivedMessage>

    fun deleteConversations(): Flow<Unit>
}