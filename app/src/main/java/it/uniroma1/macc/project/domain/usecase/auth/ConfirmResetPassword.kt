package it.uniroma1.macc.project.domain.usecase.auth

import androidx.annotation.NonNull
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.core.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConfirmResetPassword @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<ConfirmResetPassword.Params, Error, Unit>() {

    override suspend fun run(params: Params): Result<Error, Unit> {
        return try {
            identityRepository.confirmResetPassword(params.newPassword, params.confirmationCode)
            Result.Success(Unit)
        } catch (error: Error) {
            Result.Error(error)
        }
    }

    data class Params(@NonNull val newPassword: String, @NonNull val confirmationCode: String)
}