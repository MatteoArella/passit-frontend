package it.uniroma1.macc.project.domain.usecase.auth

import androidx.annotation.NonNull
import it.uniroma1.macc.project.data.repository.auth.AuthSignUpResult
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.core.UseCase
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