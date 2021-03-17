package com.github.passit.domain.repository

import androidx.annotation.NonNull
import androidx.paging.PagingData
import com.github.passit.domain.model.Insertion
import kotlinx.coroutines.flow.Flow

interface InsertionRepository {
    fun createInsertion(@NonNull title: String, @NonNull description: String, @NonNull subject: String,
                                @NonNull city: String, @NonNull state: String, @NonNull country: String): Flow<Insertion>

    fun getInsertions(@NonNull subject: String, city: String, state: String, country: String): Flow<PagingData<Insertion>>
}