package com.github.passit.data.datasource.remote.model.conversations

import com.github.passit.data.datasource.remote.model.TokenPageInfoRemoteData

data class MessagePageRemoteData(
    var items: List<MessageRemoteData> = listOf(),
    var pageInfo: TokenPageInfoRemoteData?
)