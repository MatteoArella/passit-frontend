package com.github.passit.data.datasource.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.github.passit.data.datasource.local.ConversationLocalDataSource
import com.github.passit.data.datasource.local.model.conversations.MessageRemoteKeys
import com.github.passit.data.repository.mapper.MessageRemoteToLocalMapper
import com.github.passit.domain.model.BaseMessage

@OptIn(ExperimentalPagingApi::class)
class MessageRemoteMediator(
        private val conversationId: String,
        private val conversationRemoteDataSource: ConversationRemoteDataSource,
        private val conversationLocalDataSource: ConversationLocalDataSource
): RemoteMediator<Int, BaseMessage>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, BaseMessage>): MediatorResult {
        val page: String? = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> {
                return MediatorResult.Success(true)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForNextItem(state)
                remoteKeys?.nextKey ?: return MediatorResult.Success(true)
            }
        }
        try {
            val messagePageRemoteData = conversationRemoteDataSource.getMessages(
                conversationId = conversationId,
                first = state.config.pageSize,
                nextToken = page
            )
            val messages = messagePageRemoteData.items
            val endOfPaginationReached = messagePageRemoteData.pageInfo?.hasNextPage ?: messages.isEmpty()
            conversationLocalDataSource.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    conversationLocalDataSource.cleanMessageRemoteKeys()
                    conversationLocalDataSource.clearMessages()
                }
                val nextKey = if (endOfPaginationReached) null else messagePageRemoteData.pageInfo?.nextToken
                val keys = messages.map {
                    MessageRemoteKeys(messageId = it.id, nextKey = nextKey)
                }
                conversationLocalDataSource.insertAllMessageRemoteKeys(keys)
                conversationLocalDataSource.insertAllMessages(MessageRemoteToLocalMapper.map(messages))
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForNextItem(state: PagingState<Int, BaseMessage>): MessageRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { message ->
            conversationLocalDataSource.getRemoteKeyByMessageId(message.id)
        }
    }
}