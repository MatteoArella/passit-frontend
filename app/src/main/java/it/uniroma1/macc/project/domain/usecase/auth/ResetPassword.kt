package it.uniroma1.macc.project.domain.usecase.auth

import androidx.annotation.NonNull
import it.uniroma1.macc.project.data.repository.auth.AuthResetPasswordResult
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.core.UseCase
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