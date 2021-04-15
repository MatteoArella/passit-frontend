package com.github.passit.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.passit.data.datasource.local.ConversationLocalDataSource
import com.github.passit.data.datasource.remote.ConversationRemoteDataSource
import com.github.passit.data.datasource.remote.ConversationRemoteMediator
import com.github.passit.data.datasource.remote.MessageRemoteMediator
import com.github.passit.data.repository.mapper.*
import com.github.passit.domain.model.BaseMessage
import com.github.passit.domain.model.Conversation
import com.github.passit.domain.model.Message
import com.github.passit.domain.repository.ConversationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
        private val conversationLocalDataSource: ConversationLocalDataSource,
        private val conversationRemoteDataSource: ConversationRemoteDataSource
) : ConversationRepository {
    override fun createConversation(userId: String, tutorId: String): Flow<Conversation> = flow {
        val convLink = conversationRemoteDataSource.createConversation(tutorId = tutorId)
        val conversationLocalData = ConversationRemoteToLocalMapper.map(convLink.conversation!!)
        conversationLocalData.associated = tutorId
        conversationLocalDataSource.createConversation(conversationLocalData)
        emit(ConversationRemoteToEntityMapper.map(convLink))
    }

    @ExperimentalPagingApi
    override fun getConversationPages(userId: String): Flow<PagingData<Conversation>> {
        val conversationsSourceFactory = conversationLocalDataSource.getConversationsWithAssociated().mapByPage {
            ConversationAndAssociatedLocalToEntityMapper.map(it)
        }.asPagingSourceFactory(Dispatchers.IO)
        return Pager(
            config = PagingConfig(pageSize = CONVERSATIONS_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = ConversationRemoteMediator(
                userId = userId,
                conversationLocalDataSource = conversationLocalDataSource,
                conversationRemoteDataSource = conversationRemoteDataSource
            ),
            pagingSourceFactory = conversationsSourceFactory
        ).flow
    }

    override fun createMessage(to: String, conversationId: String, content: String): Flow<BaseMessage> = flow {
        val message = conversationRemoteDataSource.createMessage(to = to, conversationId = conversationId, content = content)
        conversationLocalDataSource.createMessage(MessageRemoteToLocalMapper.map(message))
        emit(MessageRemoteToEntityMapper.map(message))
    }

    @ExperimentalPagingApi
    override fun getMessages(conversationId: String): Flow<PagingData<BaseMessage>> {
        val messagesSourceFactory = conversationLocalDataSource.getMessages(conversationId).mapByPage {
            MessageLocalToEntityMapper.map(it)
        }.asPagingSourceFactory(Dispatchers.IO)
        return Pager(
                config = PagingConfig(pageSize = MESSAGES_PAGE_SIZE, enablePlaceholders = false),
                remoteMediator = MessageRemoteMediator(
                        conversationId = conversationId,
                        conversationLocalDataSource = conversationLocalDataSource,
                        conversationRemoteDataSource = conversationRemoteDataSource
                ),
                pagingSourceFactory = messagesSourceFactory
        ).flow
    }

    override fun getUnreadMessagesCount(conversationId: String?): Flow<Int> = flow {
        emit(conversationLocalDataSource.getUnreadMessagesCount(conversationId))
    }

    override fun subscribeConversations(userId: String): Flow<Conversation> {
        return conversationRemoteDataSource.subscribeConversations(userId)
            .onEach {
                conversationLocalDataSource.createConversationWithAssociated(SubscriptionPartialConversationAndAssociatedRemoteToLocalMapper.map(it))
            }
            .map { SubscriptionConversationRemoteToEntityMapper.map(it) }
    }

    override fun subscribeMessages(userId: String): Flow<Message.ReceivedMessage> {
        return conversationRemoteDataSource.subscribeMessages(userId)
            .onEach { message ->
                message.conversationId?.let { convId -> conversationLocalDataSource.incrementUnreadMessages(convId) }
                conversationLocalDataSource.createMessage(MessageRemoteToLocalMapper.map(message))
            }
            .map { Message.ReceivedMessage(MessageRemoteToEntityMapper.map(it)) }
    }

    override fun resetUnreadMessagesCount(conversationId: String): Flow<Unit> = flow {
        conversationLocalDataSource.resetUnreadMessages(conversationId = conversationId)
        emit(Unit)
    }

    override fun deleteConversations(): Flow<Unit> = flow {
        conversationLocalDataSource.withTransaction {
            conversationLocalDataSource.cleanConversationRemoteKeys()
            conversationLocalDataSource.cleanMessageRemoteKeys()
            conversationLocalDataSource.clearConversations()
            conversationLocalDataSource.clearMessages()
        }
        emit(Unit)
    }

    companion object {
        const val CONVERSATIONS_PAGE_SIZE = 40
        const val MESSAGES_PAGE_SIZE = 80
    }
}