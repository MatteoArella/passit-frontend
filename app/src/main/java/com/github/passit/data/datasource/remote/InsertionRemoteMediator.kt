package com.github.passit.data.datasource.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.github.passit.data.datasource.local.InsertionLocalDataSource
import com.github.passit.data.datasource.local.model.InsertionRemoteKeys
import com.github.passit.data.repository.mapper.InsertionAndTutorRemoteToLocalMapper
import com.github.passit.domain.model.Insertion
import java.lang.Exception

@OptIn(ExperimentalPagingApi::class)
class InsertionRemoteMediator(
        private val subject: String,
        private val city: String,
        private val state: String,
        private val country: String,
        private val insertionRemoteDataSource: InsertionRemoteDataSource,
        private val insertionLocalDataSource: InsertionLocalDataSource
): RemoteMediator<Int, Insertion>() {
    companion object {
        private const val STARTING_PAGE_INDEX = 0
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Insertion>): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                remoteKeys?.prevKey ?: return MediatorResult.Success(true)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.nextKey ?: return MediatorResult.Success(true)
            }
        }
        try {
            val insertionPageRemoteData = insertionRemoteDataSource.getInsertions(
                    subject = this.subject,
                    city = this.city,
                    state = this.state,
                    country = this.country,
                    limit = state.config.pageSize,
                    pageNum = page
            )
            val insertions = insertionPageRemoteData.items
            val endOfPaginationReached = insertionPageRemoteData.pageInfo?.hasNextPage ?: insertions.isEmpty()
            insertionLocalDataSource.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    insertionLocalDataSource.cleanInsertionsRemoteKeys()
                    insertionLocalDataSource.clearInsertions()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = insertions.map {
                    InsertionRemoteKeys(insertionId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                insertionLocalDataSource.insertAllInsertionRemoteKeys(keys)
                insertionLocalDataSource.insertAllWithTutors(InsertionAndTutorRemoteToLocalMapper.map(insertions))
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Insertion>): InsertionRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { insertion ->
            insertionLocalDataSource.getRemoteKeyByInsertionId(insertion.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Insertion>): InsertionRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { insertion ->
            insertionLocalDataSource.getRemoteKeyByInsertionId(insertion.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Insertion>): InsertionRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let { insertion ->
                insertionLocalDataSource.withTransaction {
                    insertionLocalDataSource.getRemoteKeyByInsertionId(insertion.id)
                }
            }
        }
    }
}