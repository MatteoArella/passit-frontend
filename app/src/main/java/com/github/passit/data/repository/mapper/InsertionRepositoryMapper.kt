package com.github.passit.data.repository.mapper

import com.github.passit.core.domain.Mapper
import com.github.passit.core.extension.fromISO8601UTC
import com.github.passit.data.datasource.local.model.InsertionLocalData
import com.github.passit.data.datasource.remote.model.InsertionRemoteData
import com.github.passit.domain.model.Insertion

object InsertionRemoteToEntityMapper: Mapper<InsertionRemoteData, Insertion>() {
    override fun map(from: InsertionRemoteData): Insertion {
        return Insertion(
                id = from.id,
                title = from.title,
                description = from.description,
                subject = from.subject,
                createdAt = from.createdAt.fromISO8601UTC(),
                updatedAt = from.updatedAt.fromISO8601UTC(),
                tutor = from.tutor?.let { UserRemoteToEntityMapper.map(it) }
        )
    }
}

object InsertionRemoteToLocalMapper: Mapper<InsertionRemoteData, InsertionLocalData>() {
    override fun map(from: InsertionRemoteData): InsertionLocalData {
        return InsertionLocalData(
                insertionId = from.id ?: "",
                title = from.title ?: "",
                description = from.description ?: "",
                subject = from.subject ?: "",
                createdAt = from.createdAt ?: "",
                updatedAt = from.updatedAt ?: "",
                tutorId = from.tutor?.id ?: ""
        )
    }
}
