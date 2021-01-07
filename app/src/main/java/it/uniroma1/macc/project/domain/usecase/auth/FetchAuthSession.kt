package it.uniroma1.macc.project.domain.usecase.auth

import it.uniroma1.macc.project.data.repository.auth.AuthSessionResult
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.core.UseCase
import javax.inject.Inject

class FetchAuthSession @Inject constructor(
        private val identityRepository: IdentityRepository
) : UseCase<FetchAuthSession.Params, Error, AuthSessionResult>() {

    override suspend fun run(params: Params): Result<Error, AuthSessionResult> {
        return try {
            val result = identityRepository.fetchAuthSession()
            Result.Success(result)
        } catch (error: Throwable) {
            Result.Error(Error(error))
        }
    }

    class Params
}