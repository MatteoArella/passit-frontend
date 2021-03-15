package com.github.passit.domain.usecase.insertions

import androidx.annotation.NonNull
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.repository.InsertionRepository
import com.github.passit.core.domain.Result
import com.github.passit.core.domain.UseCase
import javax.inject.Inject

class CreateInsertion @Inject constructor(
        private val insertionRepository: InsertionRepository
) : UseCase<CreateInsertion.Params, Error, Insertion>() {

    override suspend fun run(params: Params): Result<Error, Insertion> {
        return try {
            val insertion = insertionRepository.createInsertion(params.title, params.description, params.subject)
            Result.Success(insertion)
        } catch (error: Error) {
            Result.Error(error)
        }
    }

    data class Params(@NonNull val title: String, @NonNull val description: String, @NonNull val subject: String)
}