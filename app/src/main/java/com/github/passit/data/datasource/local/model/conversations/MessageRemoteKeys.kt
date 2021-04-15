package com.github.passit.data.datasource.local.model.conversations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_remote_keys")
data class MessageRemoteKeys(
    @PrimaryKey @ColumnInfo(name = "message_id") val messageId: String,
    @ColumnInfo(name = "next_key") val nextKey: String?
)