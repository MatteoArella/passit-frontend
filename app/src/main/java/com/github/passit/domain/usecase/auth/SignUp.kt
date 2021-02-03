package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.data.repository.auth.AuthSignUpResult
import com.github.passit.domain.model.SignUpUserAttributes
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.core.Result
import com.github.passit.domain.usecase.core.UseCase
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