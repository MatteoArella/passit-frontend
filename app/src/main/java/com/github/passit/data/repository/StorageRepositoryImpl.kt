package com.github.passit.data.repository

import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.amplifyframework.storage.options.StorageUploadInputStreamOptions
import com.github.passit.data.datasource.remote.model.UploadResultRemoteData
import com.github.passit.domain.repository.StorageRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.io.InputStream
import java.net.URL
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor() : StorageRepository {

    override fun uploadStream(key: String, inputStream: InputStream): Flow<UploadResultRemoteData> = callbackFlow {
        val upload = Amplify.Storage.uploadInputStream(
            key,
            inputStream,
            StorageUploadInputStreamOptions.defaultInstance(),
            { progress -> offer(UploadResultRemoteData.UploadProgress(progress.fractionCompleted)) },
            {
                Amplify.Storage.getUrl(
                    key,
                    { result -> offer(UploadResultRemoteData.UploadResult(URL("${result.url.protocol}://${result.url.host}/public/$key"))); close() },
                    { close(it) }
                )
            },
            { close(it) }
        )
        awaitClose { upload.cancel() }
    }

    override fun uploadFile(key: String, file: File): Flow<UploadResultRemoteData> = callbackFlow {
        val upload = Amplify.Storage.uploadFile(
            key,
            file,
            StorageUploadFileOptions.defaultInstance(),
            { progress -> offer(UploadResultRemoteData.UploadProgress(progress.fractionCompleted)) },
            {
                Amplify.Storage.getUrl(
                    key,
                    { result -> offer(UploadResultRemoteData.UploadResult(URL("${result.url.protocol}://${result.url.host}/public/$key"))); close() },
                    { close(it) }
                )
            },
            { close(it) }
        )
        awaitClose { upload.cancel() }
    }
}