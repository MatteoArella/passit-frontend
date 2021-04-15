package com.github.passit.data.datasource.remote.model.conversations

import com.github.passit.data.datasource.remote.model.UserRemoteData

data class MessageRemoteData(
    var id: String,
    var content: String?,
    var author: UserRemoteData?,
    var conversation: ConversationRemoteData?,
    var conversationId: String?,
    var to: String?,
    var createdAt: String?,
    var updatedAt: String?
)