package com.github.passit.domain.repository

import androidx.annotation.NonNull
import com.github.passit.domain.model.Insertion

interface InsertionRepository {
    suspend fun createInsertion(@NonNull title: String, @NonNull description: String, @NonNull subject: String): Insertion
}