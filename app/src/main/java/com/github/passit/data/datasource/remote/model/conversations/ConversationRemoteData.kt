package com.github.passit.data.datasource.remote.model.conversations

data class ConversationRemoteData(
    var id: String,
    var associated: List<ConvLinkRemoteData>?,
    var createdAt: String?,
    var updatedAt: String?
)