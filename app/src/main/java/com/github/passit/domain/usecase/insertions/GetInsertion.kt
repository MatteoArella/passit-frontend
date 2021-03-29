package com.github.passit.domain.usecase.insertions

import androidx.annotation.NonNull
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.repository.InsertionRepository
import com.github.passit.core.domain.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInsertion @Inject constructor(
        private val insertionRepository: InsertionRepository
) : UseCase<GetInsertion.Params, Insertion>() {

    override fun run(params: Params): Flow<Insertion> =
            insertionRepository.getInsertion(params.insertionId)

    data class Params(val insertionId: String)
}