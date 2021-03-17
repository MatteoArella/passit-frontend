package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.domain.model.auth.AuthSignUp
import com.github.passit.domain.model.auth.SignUpUserAttributes
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUp @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<SignUp.Params, AuthSignUp>() {

    override fun run(params: Params): Flow<AuthSignUp> =
            identityRepository.signUp(params.email, params.password, params.attributes)

    data class Params(@NonNull val email: String, @NonNull val password: String, @NonNull val attributes: SignUpUserAttributes)
}