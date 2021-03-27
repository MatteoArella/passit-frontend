package com.github.passit.domain.usecase.auth

import android.content.Intent
import com.github.passit.core.domain.UseCase
import com.github.passit.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HandleFederatedSignInResponse @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<HandleFederatedSignInResponse.Params, Unit>() {

    override fun run(params: Params): Flow<Unit> =
            identityRepository.handleFederatedSignInResponse(params.data)

    data class Params(val data: Intent)
}