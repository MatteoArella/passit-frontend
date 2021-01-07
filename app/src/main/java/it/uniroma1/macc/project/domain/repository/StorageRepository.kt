package it.uniroma1.macc.project.domain.repository

import androidx.annotation.NonNull
import it.uniroma1.macc.project.data.repository.storage.UploadResult
import java.io.File
import java.io.InputStream

interface StorageRepository {
    suspend fun uploadStream(@NonNull key: String, @NonNull inputStream: InputStream): UploadResult

    suspend fun uploadFile(@NonNull key: String, @NonNull file: File): UploadResult
}