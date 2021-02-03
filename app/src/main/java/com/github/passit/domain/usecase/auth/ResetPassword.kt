package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.data.repository.auth.AuthResetPasswordResult
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.core.Result
import com.github.passit.domain.usecase.core.UseCase
import javax.inject.Inject

class ResetPassword @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<ResetPassword.Params, Error, AuthResetPasswordResult>() {

    override suspend fun run(params: Params): Result<Error, AuthResetPasswordResult> {
        return try {
            val result = identityRepository.resetPassword(params.email)
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    data class Params(@NonNull val email: String)
}