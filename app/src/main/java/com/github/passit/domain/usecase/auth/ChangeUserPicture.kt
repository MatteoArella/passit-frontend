package com.github.passit.domain.usecase.auth

import androidx.annotation.NonNull
import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.auth.UserAttribute
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.repository.StorageRepository
import com.github.passit.domain.model.auth.UploadResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.*
import javax.inject.Inject

class ChangeUserPicture @Inject constructor(
    private val identityRepository: IdentityRepository,
    private val storageRepository: StorageRepository
) : UseCase<ChangeUserPicture.Params, UploadResult>() {

    override fun run(params: Params): Flow<UploadResult> = flow {
        val key = "images/${UUID.randomUUID()}.jpeg"
        storageRepository.uploadFile(key, params.picture).collectIndexed{ _, uploadResult ->
            when (uploadResult) {
                is com.github.passit.data.datasource.remote.model.UploadResultRemoteData.UploadResult -> {
                    identityRepository.updateUserAttribute(UserAttribute.PICTURE, uploadResult.url.toString()).collect {
                        emit(UploadResult.Success(uploadResult.url))
                    }
                }
                is com.github.passit.data.datasource.remote.model.UploadResultRemoteData.UploadProgress -> emit(UploadResult.UploadProgress(uploadResult.progress))
            }
        }
    }

    data class Params(@NonNull val picture: File)
}