package com.github.passit.domain.usecase.auth

import com.github.passit.domain.model.auth.User
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.Result
import com.github.passit.core.domain.UseCase
import javax.inject.Inject

class FetchUserAttributes @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<FetchUserAttributes.Params, Error, User>() {

    override suspend fun run(params: Params): Result<Error, User> {
        return try {
            val result = identityRepository.fetchUserAttributes()
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    class Params
}