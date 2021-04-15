package com.github.passit.domain.model

import com.github.passit.domain.model.auth.User
import java.io.Serializable
import java.util.*

data class Conversation(
    var id: String,
    var associated: User?,
    var unread: Int?,
    var createdAt: Date?,
    var updatedAt: Date?
): Serializable