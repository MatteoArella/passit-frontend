package com.github.passit.data.datasource.remote.model

data class InsertionPageRemoteData(
    var items: List<InsertionRemoteData> = listOf(),
    var pageInfo: PageInfoRemoteData?
)