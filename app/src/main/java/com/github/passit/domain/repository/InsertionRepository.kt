package com.github.passit.domain.repository

import androidx.annotation.NonNull
import androidx.paging.PagingData
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.model.InsertionStatus
import kotlinx.coroutines.flow.Flow

interface InsertionRepository {
    fun createInsertion(@NonNull title: String, @NonNull description: String, @NonNull subject: String,
                                @NonNull city: String, @NonNull state: String, @NonNull country: String): Flow<Insertion>

    fun getInsertions(@NonNull subject: String, city: String, state: String, country: String): Flow<PagingData<Insertion>>

    fun getUserInsertions(@NonNull userID: String): Flow<PagingData<Insertion>>

    fun getInsertion(insertionId: String): Flow<Insertion>

    fun updateInsertion(insertionId: String, status: InsertionStatus?, title: String? = null, description: String? = null, subject: String? = null,
                        city: String? = null, state: String? = null, country: String? = null): Flow<Insertion>

    fun deleteInsertions(): Flow<Unit>
}