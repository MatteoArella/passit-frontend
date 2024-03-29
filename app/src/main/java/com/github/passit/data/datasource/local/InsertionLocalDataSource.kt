package com.github.passit.data.datasource.local

import androidx.paging.DataSource
import androidx.room.withTransaction
import com.github.passit.data.datasource.local.db.ApplicationDatabase
import com.github.passit.data.datasource.local.model.InsertionAndTutorLocalData
import com.github.passit.data.datasource.local.model.InsertionLocalData
import com.github.passit.data.datasource.local.model.InsertionRemoteKeys
import com.github.passit.data.datasource.local.model.UserInsertionsRemoteKeys
import javax.inject.Inject

class InsertionLocalDataSource @Inject constructor(
    private val applicationDatabase: ApplicationDatabase
) {
    suspend fun <R> withTransaction(block: suspend () -> R) = applicationDatabase.withTransaction(block)

    suspend fun createInsertion(insertion: InsertionLocalData)
        = applicationDatabase.insertionDao().create(insertion)

    suspend fun insertAllWithTutors(insertionAndTutorLocalData: List<InsertionAndTutorLocalData>)
        = applicationDatabase.insertionDao().insertAllWithTutors(insertionAndTutorLocalData)

    fun getInsertions(subject: String, city: String, state: String, country: String): DataSource.Factory<Int, InsertionAndTutorLocalData>
        = applicationDatabase.insertionDao().getInsertionsAndTutor("%$subject%", "%$city%", "%$state%", "%$country%")

    suspend fun updateInsertion(insertion: InsertionLocalData)
        = applicationDatabase.insertionDao().updateInsertion(insertion)

    suspend fun insertAllInsertionRemoteKeys(insertionRemoteKeys: List<InsertionRemoteKeys>) =
            applicationDatabase.insertionRemoteKeysDao().insertAll(insertionRemoteKeys)

    suspend fun getRemoteKeyByInsertionId(insertionId: String): InsertionRemoteKeys? =
            applicationDatabase.insertionRemoteKeysDao().getRemoteKeyByInsertionId(insertionId)

    fun getUserInsertions(userID: String): DataSource.Factory<Int, InsertionAndTutorLocalData>
            = applicationDatabase.insertionDao().getInsertionsByTutor(userID)

    suspend fun insertAllUserInsertionsRemoteKeys(insertionRemoteKeys: List<UserInsertionsRemoteKeys>) =
            applicationDatabase.userInsertionsRemoteKeysDao().insertAll(insertionRemoteKeys)

    suspend fun getRemoteKeyByUserInsertionId(insertionId: String): UserInsertionsRemoteKeys? =
            applicationDatabase.userInsertionsRemoteKeysDao().getRemoteKeyByInsertionId(insertionId)


    suspend fun cleanInsertionsRemoteKeys() = applicationDatabase.insertionRemoteKeysDao().cleanRemoteKeys()

    suspend fun clearInsertions() = applicationDatabase.insertionDao().clearInsertions()
}