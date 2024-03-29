package com.github.passit.data.repository.mapper

import com.github.passit.core.domain.Mapper
import com.github.passit.core.extension.fromISO8601UTC
import com.github.passit.core.extension.toISO8601UTC
import com.github.passit.data.datasource.local.model.InsertionAndTutorLocalData
import com.github.passit.data.datasource.local.model.InsertionLocalData
import com.github.passit.data.datasource.local.model.LocationLocalData
import com.github.passit.data.datasource.remote.model.InsertionRemoteData
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.model.InsertionStatus
import com.github.passit.domain.model.Location

object InsertionRemoteToEntityMapper: Mapper<InsertionRemoteData, Insertion>() {
    override fun map(from: InsertionRemoteData): Insertion {
        return Insertion(
                id = from.id,
                title = from.title,
                description = from.description,
                subject = from.subject,
                createdAt = from.createdAt.fromISO8601UTC(),
                updatedAt = from.updatedAt.fromISO8601UTC(),
                tutor = from.tutor?.let { UserRemoteToEntityMapper.map(it) },
                location = Location(from.location?.city, from.location?.state, from.location?.country),
                status = from.status?.let { InsertionStatus.valueOf(it) }
        )
    }
}

object LocationEntityToLocalMapper: Mapper<Location?, LocationLocalData>() {
    override fun map(from: Location?): LocationLocalData {
        return LocationLocalData(
                city = from?.city ?: "",
                country = from?.country ?: "",
                state = from?.state ?: ""
        )
    }
}

object InsertionEntityToLocalMapper: Mapper<Insertion, InsertionLocalData>() {
    override fun map(from: Insertion): InsertionLocalData {
        return InsertionLocalData(
                insertionId = from.id,
                title = from.title ?: "",
                description = from.description ?: "",
                subject = from.subject ?: "",
                location = LocationEntityToLocalMapper.map(from.location),
                tutorId = from.tutor?.id ?: "",
                status = from.status?.status ?: "OPEN",
                createdAt = from.createdAt.toISO8601UTC() ?: "",
                updatedAt = from.updatedAt.toISO8601UTC() ?: "",
        )
    }
}

object InsertionRemoteToLocalMapper: Mapper<InsertionRemoteData, InsertionLocalData>() {
    override fun map(from: InsertionRemoteData): InsertionLocalData {
        return InsertionLocalData(
                insertionId = from.id,
                title = from.title ?: "",
                description = from.description ?: "",
                subject = from.subject ?: "",
                createdAt = from.createdAt ?: "",
                updatedAt = from.updatedAt ?: "",
                tutorId = from.tutor?.id ?: "",
                location = from.location?.let { LocationLocalData(it.city ?: "", it.state ?: "", it.country ?: "") } ?: LocationLocalData("", "", ""),
                status = from.status ?: "OPEN"
        )
    }
}

object InsertionAndTutorRemoteToLocalMapper: Mapper<InsertionRemoteData, InsertionAndTutorLocalData>() {
    override fun map(from: InsertionRemoteData): InsertionAndTutorLocalData {
        return InsertionAndTutorLocalData(
                tutor = UserRemoteToLocalMapper.map(from.tutor),
                insertion = InsertionRemoteToLocalMapper.map(from)
        )
    }
}

object InsertionAndTutorLocalToEntityMapper: Mapper<InsertionAndTutorLocalData, Insertion>() {
    override fun map(from: InsertionAndTutorLocalData): Insertion {
        return Insertion(
                id = from.insertion.insertionId,
                title = from.insertion.title,
                description = from.insertion.description,
                subject = from.insertion.subject,
                createdAt = from.insertion.createdAt.fromISO8601UTC(),
                updatedAt = from.insertion.updatedAt.fromISO8601UTC(),
                tutor = UserLocalToEntityMapper.map(from.tutor),
                location = Location(from.insertion.location.city, from.insertion.location.state, from.insertion.location.country),
                status = InsertionStatus.valueOf(from.insertion.status)
        )
    }
}