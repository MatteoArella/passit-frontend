package com.github.passit.data.datasource.remote.model

import java.net.URL

sealed class UploadResultRemoteData {
    data class UploadResult(val url: URL): UploadResultRemoteData()
    data class UploadProgress(val progress: Double): UploadResultRemoteData()
}
