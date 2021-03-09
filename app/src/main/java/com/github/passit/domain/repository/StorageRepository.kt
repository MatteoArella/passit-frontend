package com.github.passit.domain.repository

import androidx.annotation.NonNull
import com.github.passit.data.datasource.remote.model.UploadResultRemoteData
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface StorageRepository {
    fun uploadStream(@NonNull key: String, @NonNull inputStream: InputStream): Flow<UploadResultRemoteData>
}