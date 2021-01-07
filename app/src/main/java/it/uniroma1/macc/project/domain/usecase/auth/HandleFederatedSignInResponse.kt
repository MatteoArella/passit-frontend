package it.uniroma1.macc.project.domain.usecase.auth

import android.app.Activity
import android.content.Intent
import com.amplifyframework.auth.result.AuthSignInResult
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.core.UseCase
import it.uniroma1.macc.project.domain.usecase.core.Result
import javax.inject.Inject

class HandleFederatedSignInResponse @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<HandleFederatedSignInResponse.Params, Error, Unit>() {

    override suspend fun run(params: Params): Result<Error, Unit> {
        identityRepository.handleFederatedSignInResponse(params.data)
        return Result.Success(Unit)
    }

    data class Params(val data: Intent)
}