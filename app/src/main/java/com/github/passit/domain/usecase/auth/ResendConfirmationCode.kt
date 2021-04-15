package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.auth.AuthSignUp
import com.github.passit.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResendConfirmationCode @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<ResendConfirmationCode.Params, AuthSignUp>() {

    override fun run(params: Params): Flow<AuthSignUp> =
            identityRepository.resendConfirmationCode(params.email)

    data class Params(@NonNull val email: String)
}