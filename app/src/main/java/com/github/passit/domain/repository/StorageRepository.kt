package com.github.passit.domain.repository

import androidx.annotation.NonNull
import com.github.passit.data.datasource.remote.model.UploadResultRemoteData
import java.io.File
import java.io.InputStream

interface StorageRepository {
    suspend fun uploadStream(@NonNull key: String, @NonNull inputStream: InputStream): UploadResultRemoteData

    suspend fun uploadFile(@NonNull key: String, @NonNull file: File): UploadResultRemoteData
}