package it.uniroma1.macc.project.domain.usecase.auth

import androidx.annotation.NonNull
import it.uniroma1.macc.project.data.repository.auth.AuthSignUpResult
import it.uniroma1.macc.project.domain.model.SignUpUserAttributes
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.core.UseCase
import javax.inject.Inject

class SignUp @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<SignUp.Params, Error, AuthSignUpResult>() {

    override suspend fun run(params: Params): Result<Error, AuthSignUpResult> {
        return try {
            val result = identityRepository.signUp(params.email, params.password, params.attributes)
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    data class Params(@NonNull val email: String, @NonNull val password: String, @NonNull val attributes: SignUpUserAttributes)
}