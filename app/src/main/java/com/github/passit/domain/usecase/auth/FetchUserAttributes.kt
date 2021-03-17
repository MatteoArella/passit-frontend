package com.github.passit.domain.usecase.auth

import com.github.passit.domain.model.auth.User
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.core.domain.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchUserAttributes @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<FetchUserAttributes.Params, User>() {

    override fun run(params: Params): Flow<User> = identityRepository.fetchUserAttributes()

    class Params
}