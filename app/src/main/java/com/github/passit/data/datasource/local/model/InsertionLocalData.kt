package com.github.passit.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insertions")
data class InsertionLocalData(
    @PrimaryKey @ColumnInfo(name = "insertion_id") var insertionId: String,
    var title: String,
    var description: String,
    var subject: String,
    @ColumnInfo(name = "tutor_id") var tutorId: String,
    @ColumnInfo(name = "created_at") var createdAt: String,
    @ColumnInfo(name = "updated_at") var updatedAt: String
)