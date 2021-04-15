package com.github.passit.ui.models.conversations

import com.github.passit.ui.models.insertions.UserView
import java.util.*

data class ConversationView(
        var id: String?,
        var associated: UserView?,
        var unread: Int?,
        var createdAt: Date?,
        var updatedAt: Date?
)