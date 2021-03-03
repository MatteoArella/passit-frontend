package com.github.passit.domain.usecase.auth

import com.github.passit.domain.model.auth.AuthSignIn
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.Result
import com.github.passit.core.domain.UseCase
import com.github.passit.domain.usecase.exception.auth.SignInError
import javax.inject.Inject

class SignIn @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<SignIn.Params, SignInError, AuthSignIn>() {

    override suspend fun run(params: Params): Result<SignInError, AuthSignIn> {
        return try {
            val result = identityRepository.signIn(params.email, params.password)
            Result.Success(result)
        } catch (error: SignInError) {
            Result.Error(error)
        }
    }

    data class Params(val email: String, val password: String)
}