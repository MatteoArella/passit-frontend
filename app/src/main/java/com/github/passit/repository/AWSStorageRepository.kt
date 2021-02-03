package com.github.passit.repository

import com.amplifyframework.core.Amplify
import com.github.passit.data.repository.storage.UploadResult
import com.github.passit.domain.repository.StorageRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.InputStream
import java.net.URL
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AWSStorageRepository @Inject constructor() : StorageRepository {

    override suspend fun uploadStream(key: String, inputStream: InputStream): UploadResult {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Storage.uploadInputStream(
                key,
                inputStream,
                {
                    Amplify.Storage.getUrl(
                        key,
                        { result -> continuation.resume(UploadResult(URL("${result.url.protocol}://${result.url.host}/public/$key"))) },
                        { error -> continuation.resumeWithException(Error(error)) }
                    )
                },
                { error -> continuation.resumeWithException(Error(error)) }
            )
        }
    }

    override suspend fun uploadFile(key: String, file: File): UploadResult {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Storage.uploadFile(
                key,
                file,
                {
                    Amplify.Storage.getUrl(
                        key,
                        { result -> continuation.resume(UploadResult(URL("${result.url.protocol}://${result.url.host}/public/$key"))) },
                        { error -> continuation.resumeWithException(Error(error)) }
                    )
                },
                { error -> continuation.resumeWithException(Error(error)) }
            )
        }
    }
}