package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.auth.AuthResetPassword
import com.github.passit.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResetPassword @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<ResetPassword.Params, AuthResetPassword>() {

    override fun run(params: Params): Flow<AuthResetPassword> =
            identityRepository.resetPassword(params.email)

    data class Params(@NonNull val email: String)
}