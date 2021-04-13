package com.github.passit.domain.usecase.insertions

import androidx.annotation.NonNull
import androidx.paging.PagingData
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.repository.InsertionRepository
import com.github.passit.core.domain.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInsertions @Inject constructor(
        private val insertionRepository: InsertionRepository
) : UseCase<GetUserInsertions.Params, PagingData<Insertion>>() {

    override fun run(params: Params): Flow<PagingData<Insertion>> =
            insertionRepository.getUserInsertions(params.userId)

    data class Params(@NonNull val userId: String)
}