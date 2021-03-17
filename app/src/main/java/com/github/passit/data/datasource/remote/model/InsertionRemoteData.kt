package com.github.passit.data.datasource.remote.model

data class InsertionRemoteData(
    var id: String,
    var title: String?,
    var description: String?,
    var subject: String?,
    var tutor: UserRemoteData?,
    var location: LocationRemoteData?,
    var createdAt: String?,
    var updatedAt: String?
)