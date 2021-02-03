package com.github.passit.domain.usecase.auth

import com.github.passit.data.repository.auth.AuthSessionResult
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.core.Result
import com.github.passit.domain.usecase.core.UseCase
import javax.inject.Inject

class FetchAuthSession @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<FetchAuthSession.Params, Error, AuthSessionResult>() {

    override suspend fun run(params: Params): Result<Error, AuthSessionResult> {
        return try {
            val result = identityRepository.fetchAuthSession()
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    class Params
}