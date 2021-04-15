package com.github.passit.data.datasource.remote.model.conversations

import com.github.passit.data.datasource.remote.model.TokenPageInfoRemoteData

data class ConvLinkPageRemoteData(
    var items: List<ConvLinkRemoteData> = listOf(),
    var pageInfo: TokenPageInfoRemoteData?
)