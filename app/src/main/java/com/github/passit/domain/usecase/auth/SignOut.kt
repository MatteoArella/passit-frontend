package com.github.passit.domain.usecase.auth

import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignOut @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<SignOut.Params, Unit>() {

    override fun run(params: Params): Flow<Unit> = identityRepository.signOut()

    class Params
}