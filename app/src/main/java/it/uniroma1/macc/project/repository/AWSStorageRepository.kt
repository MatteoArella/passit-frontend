package it.uniroma1.macc.project.repository

import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.amplifyframework.storage.options.StorageUploadInputStreamOptions
import com.amplifyframework.storage.s3.operation.AWSS3StorageUploadFileOperation
import it.uniroma1.macc.project.data.repository.storage.UploadResult
import it.uniroma1.macc.project.domain.repository.StorageRepository
import it.uniroma1.macc.project.domain.usecase.core.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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