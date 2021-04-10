package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.auth.UserAttribute
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.File
import java.net.URL
import java.util.*
import javax.inject.Inject

class ChangeUserPicture @Inject constructor(
    private val identityRepository: IdentityRepository,
    private val storageRepository: StorageRepository
) : UseCase<ChangeUserPicture.Params, URL>() {

    override fun run(params: Params): Flow<URL> = flow {
        val key = "images/${UUID.randomUUID()}.jpeg"
        storageRepository.uploadFile(key, params.picture).collect { uploadResult ->
            identityRepository.updateUserAttribute(UserAttribute.PICTURE, uploadResult.url.toString()).collect {
                emit(uploadResult.url)
            }
        }
    }

    data class Params(@NonNull val picture: File)
}