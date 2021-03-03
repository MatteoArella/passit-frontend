package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.Result
import com.github.passit.core.domain.UseCase
import javax.inject.Inject

class ConfirmResetPassword @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<ConfirmResetPassword.Params, Error, Unit>() {

    override suspend fun run(params: Params): Result<Error, Unit> {
        return try {
            identityRepository.confirmResetPassword(params.newPassword, params.confirmationCode)
            Result.Success(Unit)
        } catch (error: Error) {
            Result.Error(error)
        }
    }

    data class Params(@NonNull val newPassword: String, @NonNull val confirmationCode: String)
}