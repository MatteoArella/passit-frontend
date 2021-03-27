package com.github.passit.ui.mapper

import com.github.passit.core.domain.Mapper
import com.github.passit.domain.model.Conversation
import com.github.passit.domain.model.Message
import com.github.passit.ui.models.conversations.BaseMessageView
import com.github.passit.ui.models.conversations.ConversationView
import com.github.passit.ui.models.conversations.MessageView

object ConversationEntityToUIMapper: Mapper<Conversation, ConversationView>() {
    override fun map(from: Conversation): ConversationView {
        return ConversationView(
                id = from.id,
                associated = from.associated?.let { UserEntityToUIMapper.map(it) },
                unread = from.unread,
                createdAt = from.createdAt,
                updatedAt = from.updatedAt
        )
    }
}

object MessageEntityToUIMapper: Mapper<Message, MessageView.Message>() {
    override fun map(from: Message): MessageView.Message {
        return when (from) {
            is Message.SentMessage -> {
                MessageView.Message.SentMessage(
                    BaseMessageView(
                        id = from.message.id,
                        author = from.message.author?.let { UserEntityToUIMapper.map(it) },
                        content = from.message.content,
                        createdAt = from.message.createdAt,
                        updatedAt = from.message.updatedAt
                    )
                )
            }
            is Message.ReceivedMessage -> {
                MessageView.Message.ReceivedMessage(
                    BaseMessageView(
                        id = from.message.id,
                        author = from.message.author?.let { UserEntityToUIMapper.map(it) },
                        content = from.message.content,
                        createdAt = from.message.createdAt,
                        updatedAt = from.message.updatedAt
                    )
                )
            }
        }
    }
}
