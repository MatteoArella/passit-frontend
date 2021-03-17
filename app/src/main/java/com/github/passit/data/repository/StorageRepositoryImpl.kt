package com.github.passit.data.repository

import com.amplifyframework.core.Amplify
import com.github.passit.data.datasource.remote.model.UploadResultRemoteData
import com.github.passit.domain.repository.StorageRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.InputStream
import java.net.URL
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor() : StorageRepository {

    override fun uploadStream(key: String, inputStream: InputStream): Flow<UploadResultRemoteData> = callbackFlow {
        Amplify.Storage.uploadInputStream(
            key,
            inputStream,
            {
                Amplify.Storage.getUrl(
                    key,
                    { result -> offer(UploadResultRemoteData(URL("${result.url.protocol}://${result.url.host}/public/$key"))); close() },
                    { close(it) }
                )
            },
            { close(it) }
        )
        awaitClose()
    }
}