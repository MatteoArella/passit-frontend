package com.github.passit.data.datasource.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class InsertionAndTutorLocalData(
    @Embedded val insertion: InsertionLocalData,
    @Relation(
            parentColumn = "tutor_id",
            entityColumn = "user_id"
    )
    val tutor: UserLocalData
)