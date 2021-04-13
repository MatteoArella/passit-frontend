package com.github.passit.domain.model.auth

import java.net.URL

sealed class UploadResult {
    data class Success(val url: URL): UploadResult()
    data class UploadProgress(val progress: Double): UploadResult()
}
