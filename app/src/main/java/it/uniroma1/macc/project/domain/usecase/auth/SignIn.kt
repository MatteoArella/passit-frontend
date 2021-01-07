package it.uniroma1.macc.project.domain.usecase.auth

import it.uniroma1.macc.project.data.repository.auth.AuthSignInResult
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.core.UseCase
import it.uniroma1.macc.project.domain.usecase.exception.auth.SignInError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
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