package com.github.passit.domain.usecase.auth

import android.app.Activity
import com.github.passit.data.repository.auth.AuthSignInResult
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.core.UseCase
import com.github.passit.domain.usecase.core.Result
import javax.inject.Inject

class SignInWithGoogle @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<SignInWithGoogle.Params, Error, AuthSignInResult>() {

    override suspend fun run(params: Params): Result<Error, AuthSignInResult> {
        return try {
            val result = identityRepository.signInWithGoogle(params.context)
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    data class Params(val context: Activity)
}