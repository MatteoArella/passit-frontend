package com.github.passit.domain.usecase.auth

import android.app.Activity
import com.github.passit.domain.model.auth.AuthSignIn
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInWithGoogle @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<SignInWithGoogle.Params, AuthSignIn>() {

    override fun run(params: Params): Flow<AuthSignIn> =
            identityRepository.signInWithGoogle(params.context)

    data class Params(val context: Activity)
}