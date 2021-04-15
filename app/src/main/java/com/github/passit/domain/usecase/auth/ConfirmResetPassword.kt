package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.core.domain.UseCase
import com.github.passit.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConfirmResetPassword @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<ConfirmResetPassword.Params, Unit>() {

    override fun run(params: Params): Flow<Unit> =
            identityRepository.confirmResetPassword(params.newPassword, params.confirmationCode)

    data class Params(@NonNull val newPassword: String, @NonNull val confirmationCode: String)
}