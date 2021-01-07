package it.uniroma1.macc.project.domain.usecase.auth

import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.core.UseCase
import javax.inject.Inject

class SignOut @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<SignOut.Params, Error, Unit>() {

    override suspend fun run(params: Params): Result<Error, Unit> {
        return try {
            val result = identityRepository.signOut()
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    class Params
}