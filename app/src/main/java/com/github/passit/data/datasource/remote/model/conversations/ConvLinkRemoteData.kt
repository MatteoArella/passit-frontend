package com.github.passit.data.datasource.remote.model.conversations

import com.github.passit.data.datasource.remote.model.UserRemoteData

data class ConvLinkRemoteData(
    var id: String,
    var user: UserRemoteData?,
    var userId: String?,
    var conversation: ConversationRemoteData?,
    var createdAt: String?,
    var updatedAt: String?
)