package com.github.passit.data.datasource.local.model.conversations

import androidx.room.Embedded
import androidx.room.Relation
import com.github.passit.data.datasource.local.model.UserLocalData

data class MessageAndAuthorLocalData(
    @Embedded val message: MessageLocalData,
    @Relation(
                parentColumn = "author_id",
                entityColumn = "user_id"
        )
        val author: UserLocalData
)