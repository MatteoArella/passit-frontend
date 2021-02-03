package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.data.repository.auth.AuthSignUpResult
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.core.Result
import com.github.passit.domain.usecase.core.UseCase
import javax.inject.Inject

class ConfirmSignUp @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<ConfirmSignUp.Params, Error, AuthSignUpResult>() {

    override suspend fun run(params: Params): Result<Error, AuthSignUpResult> {
        return try {
            val result = identityRepository.confirmSignUp(params.username, params.confirmationCode)
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    data class Params(@NonNull val username: String, @NonNull val confirmationCode: String)
}