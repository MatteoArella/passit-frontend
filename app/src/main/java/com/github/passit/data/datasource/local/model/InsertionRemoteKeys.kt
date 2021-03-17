package com.github.passit.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insertion_remote_keys")
data class InsertionRemoteKeys(
    @PrimaryKey @ColumnInfo(name = "insertion_id") var insertionId: String,
    @ColumnInfo(name = "prev_key") var prevKey: Int?,
    @ColumnInfo(name = "next_key") var nextKey: Int?
)