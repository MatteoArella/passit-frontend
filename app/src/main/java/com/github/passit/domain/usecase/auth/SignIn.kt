package com.github.passit.domain.usecase.auth

import com.github.passit.data.repository.auth.AuthSignInResult
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.core.Result
import com.github.passit.domain.usecase.core.UseCase
import com.github.passit.domain.usecase.exception.auth.SignInError
import javax.inject.Inject

class SignIn @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<SignIn.Params, SignInError, AuthSignInResult>() {

    override suspend fun run(params: Params): Result<SignInError, AuthSignInResult> {
        return try {
            val result = identityRepository.signIn(params.email, params.password)
            Result.Success(result)
        } catch (error: SignInError) {
            Result.Error(error)
        }
    }

    data class Params(val email: String, val password: String)
}