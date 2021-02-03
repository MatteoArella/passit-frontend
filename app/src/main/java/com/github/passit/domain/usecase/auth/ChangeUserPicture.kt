package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.data.repository.auth.UserAttribute
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.repository.StorageRepository
import com.github.passit.domain.usecase.core.Result
import com.github.passit.domain.usecase.core.UseCase
import kotlinx.coroutines.flow.*
import java.io.InputStream
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