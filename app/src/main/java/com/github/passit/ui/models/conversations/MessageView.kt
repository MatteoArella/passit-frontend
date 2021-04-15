package com.github.passit.ui.models.conversations

import com.github.passit.ui.models.insertions.UserView
import java.util.*

data class BaseMessageView(
        var id: String?,
        var author: UserView?,
        var content: String,
        var createdAt: Date?,
        var updatedAt: Date?
)

sealed class MessageView {
    sealed class Message(var message: BaseMessageView): MessageView() {
        class SentMessage(message: BaseMessageView): Message(message)
        class ReceivedMessage(message: BaseMessageView): Message(message)
    }

    data class SeparatorItem(var date: Date): MessageView()
}
