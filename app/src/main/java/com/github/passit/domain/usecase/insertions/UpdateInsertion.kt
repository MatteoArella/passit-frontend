package com.github.passit.domain.usecase.insertions

import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.model.InsertionStatus
import com.github.passit.domain.repository.InsertionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateInsertion @Inject constructor(
        private val insertionRepository: InsertionRepository
) : UseCase<UpdateInsertion.Params, Insertion>() {

    override fun run(params: Params): Flow<Insertion> =
            insertionRepository.updateInsertion(params.insertionId, params.status, params.title, params.description, params.subject, params.city, params.state, params.country)

    data class Params(val insertionId: String, val status: InsertionStatus?, val title: String?,
                      val description: String?, val  subject: String?,
                      val city: String?, val state: String?, val country: String?)
}