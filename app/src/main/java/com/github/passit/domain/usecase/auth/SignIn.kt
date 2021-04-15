package com.github.passit.domain.usecase.auth

import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.auth.AuthSignIn
import com.github.passit.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignIn @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<SignIn.Params, AuthSignIn>() {

    override fun run(params: Params): Flow<AuthSignIn> =
            identityRepository.signIn(params.email, params.password)

    data class Params(val email: String, val password: String)
}