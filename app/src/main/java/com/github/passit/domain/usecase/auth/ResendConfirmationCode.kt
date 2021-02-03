package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.data.repository.auth.AuthSignUpResult
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.core.Result
import com.github.passit.domain.usecase.core.UseCase
import javax.inject.Inject

class ResendConfirmationCode @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<ResendConfirmationCode.Params, Error, AuthSignUpResult>() {

    override suspend fun run(params: Params): Result<Error, AuthSignUpResult> {
        return try {
            val result = identityRepository.resendConfirmationCode(params.email)
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    data class Params(@NonNull val email: String)
}