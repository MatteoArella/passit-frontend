package com.github.passit.ui.models.insertions

import java.io.Serializable

data class InsertionSearchView(
        val subject: String,
        val city: String,
        val state: String,
        val country: String
): Serializable