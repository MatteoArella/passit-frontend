package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.domain.model.auth.AuthSignUp
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.Result
import com.github.passit.core.domain.UseCase
import javax.inject.Inject

class ResendConfirmationCode @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<ResendConfirmationCode.Params, Error, AuthSignUp>() {

    override suspend fun run(params: Params): Result<Error, AuthSignUp> {
        return try {
            val result = identityRepository.resendConfirmationCode(params.email)
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    data class Params(@NonNull val email: String)
}