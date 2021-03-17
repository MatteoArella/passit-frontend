package com.github.passit.data.datasource.local.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface InsertionRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(insertionRemoteKeys: List<InsertionRemoteKeys>)

    @Query("SELECT * FROM insertion_remote_keys WHERE insertion_id = :insertionId")
    suspend fun getRemoteKeyByInsertionId(insertionId: String): InsertionRemoteKeys?

    @Query("DELETE FROM insertion_remote_keys")
    suspend fun cleanRemoteKeys()
}