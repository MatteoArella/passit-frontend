package com.github.passit.data.repository

import androidx.annotation.NonNull
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.passit.data.datasource.local.InsertionLocalDataSource
import com.github.passit.data.datasource.remote.InsertionRemoteDataSource
import com.github.passit.data.datasource.remote.InsertionRemoteMediator
import com.github.passit.data.datasource.remote.UserInsertionsRemoteMediator
import com.github.passit.data.repository.mapper.InsertionAndTutorLocalToEntityMapper
import com.github.passit.data.repository.mapper.InsertionRemoteToEntityMapper
import com.github.passit.data.repository.mapper.InsertionRemoteToLocalMapper
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.repository.InsertionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertionRepositoryImpl @Inject constructor(
    private val insertionLocalDataSource: InsertionLocalDataSource,
    private val insertionRemoteDataSource: InsertionRemoteDataSource
) : InsertionRepository {
    override fun createInsertion(@NonNull title: String, @NonNull description: String, @NonNull subject: String,
                                         @NonNull city: String, @NonNull state: String, @NonNull country: String): Flow<Insertion> = flow {
        val insertion = insertionRemoteDataSource.createInsertion(title, description, subject, city, state, country)
        insertionLocalDataSource.createInsertion(InsertionRemoteToLocalMapper.map(insertion))
        emit(InsertionRemoteToEntityMapper.map(insertion))
    }

    @ExperimentalPagingApi
    override fun getInsertions(@NonNull subject: String, city: String, state: String, country: String): Flow<PagingData<Insertion>> {
        val insertionsSourceFactory = insertionLocalDataSource.getInsertions(subject, city, state, country).mapByPage {
            InsertionAndTutorLocalToEntityMapper.map(it)
        }.asPagingSourceFactory(Dispatchers.IO)
        return Pager(
            config = PagingConfig(pageSize = INSERTIONS_PAGE_SIZE, enablePlaceholders = true),
            remoteMediator = InsertionRemoteMediator(
                    subject = subject,
                    city = city,
                    state = state,
                    country = country,
                    insertionLocalDataSource = insertionLocalDataSource,
                    insertionRemoteDataSource = insertionRemoteDataSource
            ),
            pagingSourceFactory = insertionsSourceFactory
        ).flow
    }

    @ExperimentalPagingApi
    override fun getUserInsertions(@NonNull userID: String): Flow<PagingData<Insertion>> {
        val insertionsSourceFactory = insertionLocalDataSource.getUserInsertions(userID).mapByPage {
            InsertionAndTutorLocalToEntityMapper.map(it)
        }.asPagingSourceFactory(Dispatchers.IO)
        return Pager(
                config = PagingConfig(pageSize = INSERTIONS_PAGE_SIZE, enablePlaceholders = true),
                remoteMediator = UserInsertionsRemoteMediator(
                        userId = userID,
                        insertionRemoteDataSource = insertionRemoteDataSource,
                        insertionLocalDataSource = insertionLocalDataSource
                ),
                pagingSourceFactory = insertionsSourceFactory
        ).flow
    }

    override fun getInsertion(insertionId: String): Flow<Insertion> = flow {
        val insertion = insertionRemoteDataSource.getInsertion(insertionId)
        // Update the local data source after fetching from the remote one
        insertionLocalDataSource.createInsertion(InsertionRemoteToLocalMapper.map(insertion))
        emit(InsertionRemoteToEntityMapper.map(insertion))
    }

    override fun deleteInsertions(): Flow<Unit> = flow {
        insertionLocalDataSource.withTransaction {
            insertionLocalDataSource.cleanInsertionsRemoteKeys()
            insertionLocalDataSource.clearInsertions()
        }
        emit(Unit)
    }

    companion object {
        const val INSERTIONS_PAGE_SIZE = 20
    }
}