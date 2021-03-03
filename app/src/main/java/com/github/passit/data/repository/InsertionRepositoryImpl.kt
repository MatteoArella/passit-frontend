package com.github.passit.data.repository

import androidx.annotation.NonNull
import com.github.passit.data.datasource.local.InsertionLocalDataSource
import com.github.passit.data.datasource.remote.InsertionRemoteDataSource
import com.github.passit.data.repository.mapper.InsertionRemoteToEntityMapper
import com.github.passit.data.repository.mapper.InsertionRemoteToLocalMapper
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.repository.InsertionRepository
import javax.inject.Inject

class InsertionRepositoryImpl @Inject constructor(
    private val insertionLocalDataSource: InsertionLocalDataSource,
    private val insertionRemoteDataSource: InsertionRemoteDataSource
) : InsertionRepository {
    override suspend fun createInsertion(@NonNull title: String, @NonNull description: String, @NonNull subject: String): Insertion {
        val insertion = insertionRemoteDataSource.createInsertion(title, description, subject)
        insertionLocalDataSource.createInsertion(InsertionRemoteToLocalMapper.map(insertion))
        return InsertionRemoteToEntityMapper.map(insertion)
    }
}