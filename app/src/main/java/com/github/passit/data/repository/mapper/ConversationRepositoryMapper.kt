package com.github.passit.data.repository.mapper

import com.github.passit.core.domain.Mapper
import com.github.passit.core.extension.fromISO8601UTC
import com.github.passit.data.datasource.local.model.UserLocalData
import com.github.passit.data.datasource.local.model.conversations.ConversationAndAssociatedLocalData
import com.github.passit.data.datasource.local.model.conversations.ConversationPartialLocalData
import com.github.passit.data.datasource.local.model.conversations.MessageLocalData
import com.github.passit.data.datasource.local.model.conversations.PartialConversationAndAssociatedLocalData
import com.github.passit.data.datasource.remote.model.conversations.ConvLinkRemoteData
import com.github.passit.data.datasource.remote.model.conversations.ConversationRemoteData
import com.github.passit.data.datasource.remote.model.conversations.MessageRemoteData
import com.github.passit.domain.model.BaseMessage
import com.github.passit.domain.model.Conversation

object ConversationRemoteToLocalMapper: Mapper<ConversationRemoteData, ConversationPartialLocalData>() {
    override fun map(from: ConversationRemoteData): ConversationPartialLocalData {
        return ConversationPartialLocalData(
            conversationId = from.id,
            associated = "",
            createdAt = from.createdAt ?: "",
            updatedAt = from.updatedAt ?: ""
        )
    }
}

object PartialConversationAndAssociatedRemoteToLocalMapper: Mapper<ConvLinkRemoteData, PartialConversationAndAssociatedLocalData>() {
    override fun map(from: ConvLinkRemoteData): PartialConversationAndAssociatedLocalData {
        return PartialConversationAndAssociatedLocalData(
            associated = UserRemoteToLocalMapper.map(from.conversation?.associated?.first()?.user),
            conversation = ConversationPartialLocalData(
                    conversationId = from.conversation?.id ?: "",
                    associated = from.conversation?.associated?.first()?.user?.id ?: "",
                    createdAt = from.conversation?.createdAt ?: "",
                    updatedAt = from.conversation?.updatedAt ?: ""
            )
        )
    }
}

object SubscriptionPartialConversationAndAssociatedRemoteToLocalMapper: Mapper<ConvLinkRemoteData, PartialConversationAndAssociatedLocalData>() {
    override fun map(from: ConvLinkRemoteData): PartialConversationAndAssociatedLocalData {
        val associated = from.conversation?.associated?.first { it.user?.id != from.user?.id }
        return PartialConversationAndAssociatedLocalData(
            associated = UserRemoteToLocalMapper.map(associated?.user),
            conversation = ConversationPartialLocalData(
                conversationId = from.conversation?.id ?: "",
                associated = associated?.user?.id ?: "",
                createdAt = from.conversation?.createdAt ?: "",
                updatedAt = from.conversation?.updatedAt ?: ""
            )
        )
    }
}

object ConversationAndAssociatedLocalToEntityMapper: Mapper<ConversationAndAssociatedLocalData, Conversation>() {
    override fun map(from: ConversationAndAssociatedLocalData): Conversation {
        return Conversation(
            id = from.conversation.conversationId,
            associated = UserLocalToEntityMapper.map(from.associated),
            unread = if (from.conversation.unreadMsg != 0) from.conversation.unreadMsg else null,
            createdAt = from.conversation.createdAt.fromISO8601UTC(),
            updatedAt = from.conversation.updatedAt.fromISO8601UTC()
        )
    }
}

object ConversationRemoteToEntityMapper: Mapper<ConvLinkRemoteData, Conversation>() {
    override fun map(from: ConvLinkRemoteData): Conversation {
        val associated = from.conversation?.associated?.first { it.user?.id == from.user?.id }
        return Conversation(
                id = from.conversation?.id ?: "",
                associated = associated?.user?.let { UserRemoteToEntityMapper.map(it) },
                unread = null,
                createdAt = from.createdAt.fromISO8601UTC(),
                updatedAt = from.updatedAt.fromISO8601UTC()
        )
    }
}

object SubscriptionConversationRemoteToEntityMapper: Mapper<ConvLinkRemoteData, Conversation>() {
    override fun map(from: ConvLinkRemoteData): Conversation {
        val associated = from.conversation?.associated?.first { it.user?.id != from.user?.id }
        return Conversation(
            id = from.conversation?.id ?: "",
            associated = associated?.user?.let { UserRemoteToEntityMapper.map(it) },
            unread = null,
            createdAt = from.createdAt.fromISO8601UTC(),
            updatedAt = from.updatedAt.fromISO8601UTC()
        )
    }
}

object MessageRemoteToLocalMapper: Mapper<MessageRemoteData, MessageLocalData>() {
    override fun map(from: MessageRemoteData): MessageLocalData {
        return MessageLocalData(
                id = from.id,
                content = from.content ?: "",
                authorId = from.author?.id ?: "",
                conversationId = from.conversation?.id ?: from.conversationId ?: "",
                createdAt = from.createdAt ?: "",
                updatedAt = from.updatedAt ?: ""
        )
    }
}

object MessageRemoteToEntityMapper: Mapper<MessageRemoteData, BaseMessage>() {
    override fun map(from: MessageRemoteData): BaseMessage {
        return BaseMessage(
                id = from.id,
                author = from.author?.let { UserRemoteToEntityMapper.map(it) },
                content = from.content ?: "",
                conversation = Conversation(
                    from.conversationId ?: "",
                    associated = from.author?.let { UserRemoteToEntityMapper.map(it) },
                    unread = null,
                    createdAt = null,
                    updatedAt = null
                ),
                createdAt = from.createdAt.fromISO8601UTC(),
                updatedAt = from.updatedAt.fromISO8601UTC()
        )
    }
}

object MessageLocalToEntityMapper: Mapper<MessageLocalData, BaseMessage>() {
    override fun map(from: MessageLocalData): BaseMessage {
        return BaseMessage(
                id = from.id,
                author = UserLocalToEntityMapper.map(UserLocalData(userId = from.authorId)),
                content = from.content,
                conversation = Conversation(
                        from.conversationId,
                        associated = null,
                        unread = null,
                        createdAt = null,
                        updatedAt = null
                ),
                createdAt = from.createdAt.fromISO8601UTC(),
                updatedAt = from.updatedAt.fromISO8601UTC()
        )
    }
}