package com.github.passit.data.datasource.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.github.passit.data.datasource.local.ConversationLocalDataSource
import com.github.passit.data.datasource.local.model.conversations.ConversationRemoteKeys
import com.github.passit.data.repository.mapper.PartialConversationAndAssociatedRemoteToLocalMapper
import com.github.passit.domain.model.Conversation

@OptIn(ExperimentalPagingApi::class)
class ConversationRemoteMediator(
        private val userId: String,
        private val conversationRemoteDataSource: ConversationRemoteDataSource,
        private val conversationLocalDataSource: ConversationLocalDataSource
): RemoteMediator<Int, Conversation>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Conversation>): MediatorResult {
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
            val convLinkPageRemoteData = conversationRemoteDataSource.getConversations(
                first = state.config.pageSize,
                nextToken = page
            )
            val conversations = convLinkPageRemoteData.items
            val endOfPaginationReached = convLinkPageRemoteData.pageInfo?.hasNextPage ?: conversations.isEmpty()
            conversationLocalDataSource.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    conversationLocalDataSource.cleanConversationRemoteKeys()
                }
                val nextKey = if (endOfPaginationReached) null else convLinkPageRemoteData.pageInfo?.nextToken
                val keys = conversations.map {
                    ConversationRemoteKeys(conversationId = it.conversation?.id!!, nextKey = nextKey)
                }
                conversationLocalDataSource.insertAllConversationRemoteKeys(keys)
                conversations.onEach { link ->
                    val associated = link.conversation?.associated?.first { it.user?.id != userId }
                    link.conversation?.associated = associated?.let { listOf(associated) }
                }
                conversationLocalDataSource.insertAllWithAssociated(PartialConversationAndAssociatedRemoteToLocalMapper.map(conversations))
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForNextItem(state: PagingState<Int, Conversation>): ConversationRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { conversation ->
            conversationLocalDataSource.getRemoteKeyByConversationId(conversation.id)
        }
    }
}