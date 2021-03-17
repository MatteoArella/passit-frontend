package com.github.passit.domain.usecase.insertions

import androidx.annotation.NonNull
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.repository.InsertionRepository
import com.github.passit.core.domain.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateInsertion @Inject constructor(
        private val insertionRepository: InsertionRepository
) : UseCase<CreateInsertion.Params, Insertion>() {

    override fun run(params: Params): Flow<Insertion> =
            insertionRepository.createInsertion(params.title, params.description, params.subject, params.city, params.state, params.country)

    data class Params(@NonNull val title: String, @NonNull val description: String, @NonNull val subject: String,
                      @NonNull val city: String, @NonNull val state: String, @NonNull val country: String)
}