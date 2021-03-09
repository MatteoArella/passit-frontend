package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.domain.model.auth.AuthSignUp
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConfirmSignUp @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<ConfirmSignUp.Params, AuthSignUp>() {

    override fun run(params: Params): Flow<AuthSignUp> =
            identityRepository.confirmSignUp(params.username, params.confirmationCode)

    data class Params(@NonNull val username: String, @NonNull val confirmationCode: String)
}