package com.github.passit.data.datasource.local.model.conversations

import androidx.room.Embedded
import androidx.room.Relation
import com.github.passit.data.datasource.local.model.UserLocalData

data class PartialConversationAndAssociatedLocalData(
        @Embedded val conversation: ConversationPartialLocalData,
        @Relation(
                parentColumn = "associated",
                entityColumn = "user_id"
        )
        val associated: UserLocalData
)