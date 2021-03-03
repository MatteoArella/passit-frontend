package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.domain.model.auth.AuthResetPassword
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.Result
import com.github.passit.core.domain.UseCase
import javax.inject.Inject

class ResetPassword @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<ResetPassword.Params, Error, AuthResetPassword>() {

    override suspend fun run(params: Params): Result<Error, AuthResetPassword> {
        return try {
            val result = identityRepository.resetPassword(params.email)
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    data class Params(@NonNull val email: String)
}