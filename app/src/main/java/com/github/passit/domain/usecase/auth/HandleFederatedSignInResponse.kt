package com.github.passit.domain.usecase.auth

import android.content.Intent
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.core.UseCase
import com.github.passit.domain.usecase.core.Result
import javax.inject.Inject

class HandleFederatedSignInResponse @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<HandleFederatedSignInResponse.Params, Error, Unit>() {

    override suspend fun run(params: Params): Result<Error, Unit> {
        identityRepository.handleFederatedSignInResponse(params.data)
        return Result.Success(Unit)
    }

    data class Params(val data: Intent)
}