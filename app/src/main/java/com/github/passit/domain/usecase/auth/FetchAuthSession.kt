package com.github.passit.domain.usecase.auth

import com.github.passit.domain.model.auth.AuthSession
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.Result
import com.github.passit.core.domain.UseCase
import javax.inject.Inject

class FetchAuthSession @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<FetchAuthSession.Params, Error, AuthSession>() {

    override suspend fun run(params: Params): Result<Error, AuthSession> {
        return try {
            val result = identityRepository.fetchAuthSession()
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    class Params
}