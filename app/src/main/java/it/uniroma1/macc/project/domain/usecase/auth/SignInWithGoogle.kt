package it.uniroma1.macc.project.domain.usecase.auth

import android.app.Activity
import it.uniroma1.macc.project.data.repository.auth.AuthSignInResult
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.core.UseCase
import it.uniroma1.macc.project.domain.usecase.core.Result
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