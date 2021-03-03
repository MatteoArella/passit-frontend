package com.github.passit.domain.usecase.auth

import android.app.Activity
import com.github.passit.domain.model.auth.AuthSignIn
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.UseCase
import com.github.passit.core.domain.Result
import javax.inject.Inject

class SignInWithGoogle @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<SignInWithGoogle.Params, Error, AuthSignIn>() {

    override suspend fun run(params: Params): Result<Error, AuthSignIn> {
        return try {
            val result = identityRepository.signInWithGoogle(params.context)
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    data class Params(val context: Activity)
}