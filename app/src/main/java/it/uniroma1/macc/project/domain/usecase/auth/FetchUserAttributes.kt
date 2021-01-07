package it.uniroma1.macc.project.domain.usecase.auth

import it.uniroma1.macc.project.domain.model.User
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.core.UseCase
import javax.inject.Inject

class FetchUserAttributes @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<FetchUserAttributes.Params, Error, User>() {

    override suspend fun run(params: Params): Result<Error, User> {
        return try {
            val result = identityRepository.fetchUserAttributes()
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    class Params
}