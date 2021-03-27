package com.github.passit.domain.model

import com.github.passit.domain.model.auth.User
import java.io.Serializable
import java.util.*

data class BaseMessage(
    var id: String,
    var author: User?,
    var content: String,
    var conversation: Conversation?,
    var createdAt: Date?,
    var updatedAt: Date?
): Serializable

sealed class Message: Serializable {
    data class SentMessage(
        var message: BaseMessage
    ): Message(), Serializable
    data class ReceivedMessage(
        var message: BaseMessage
    ): Message(), Serializable
}