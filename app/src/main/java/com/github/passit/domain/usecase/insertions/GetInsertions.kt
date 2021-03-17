package com.github.passit.domain.usecase.insertions

import androidx.annotation.NonNull
import androidx.paging.PagingData
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.repository.InsertionRepository
import com.github.passit.core.domain.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInsertions @Inject constructor(
        private val insertionRepository: InsertionRepository
) : UseCase<GetInsertions.Params, PagingData<Insertion>>() {

    override fun run(params: Params): Flow<PagingData<Insertion>> =
            insertionRepository.getInsertions(params.subject, params.city, params.state, params.country)

    data class Params(@NonNull val subject: String, val city: String, val state: String, val country: String)
}