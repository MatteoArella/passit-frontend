package com.github.passit.domain.usecase.auth

import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.auth.AuthSession
import com.github.passit.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchAuthSession @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<FetchAuthSession.Params, AuthSession>() {

    override fun run(params: Params): Flow<AuthSession> = identityRepository.fetchAuthSession()

    class Params
}