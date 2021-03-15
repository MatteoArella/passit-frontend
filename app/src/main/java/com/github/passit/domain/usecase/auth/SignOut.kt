package com.github.passit.domain.usecase.auth

import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.Result
import com.github.passit.core.domain.UseCase
import javax.inject.Inject

class SignOut @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<SignOut.Params, Error, Unit>() {

    override suspend fun run(params: Params): Result<Error, Unit> {
        return try {
            val result = identityRepository.signOut()
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    class Params
}