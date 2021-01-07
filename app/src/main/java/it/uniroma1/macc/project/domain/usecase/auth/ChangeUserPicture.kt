package it.uniroma1.macc.project.domain.usecase.auth

import androidx.annotation.NonNull
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import it.uniroma1.macc.project.data.repository.auth.UserAttribute
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.repository.StorageRepository
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.core.UseCase
import it.uniroma1.macc.project.domain.usecase.core.onStateLoading
import it.uniroma1.macc.project.domain.usecase.core.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.net.URL
import java.util.*
import javax.inject.Inject

class ChangeUserPicture @Inject constructor(
    private val identityRepository: IdentityRepository,
    private val storageRepository: StorageRepository
) : UseCase<ChangeUserPicture.Params, Error, URL>() {

    override suspend fun run(params: Params): Result<Error, URL> {
        val key = "images/${UUID.randomUUID()}.png"
        return try {
            val uploadResult = storageRepository.uploadStream(key, params.stream)
            identityRepository.updateUserAttribute(UserAttribute.PICTURE, uploadResult.url.toString())
            Result.Success(uploadResult.url)
        } catch (error: Error) {
            Result.Error(error)
        }
    }

    data class Params(@NonNull val stream: InputStream)
}