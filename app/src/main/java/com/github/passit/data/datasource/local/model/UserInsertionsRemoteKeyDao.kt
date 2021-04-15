package com.github.passit.data.datasource.local.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserInsertionsRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userInsertionsRemoteKeys: List<UserInsertionsRemoteKeys>)

    @Query("SELECT * FROM insertion_remote_keys WHERE insertion_id = :insertionId")
    suspend fun getRemoteKeyByInsertionId(insertionId: String): UserInsertionsRemoteKeys?

    @Query("DELETE FROM insertion_remote_keys")
    suspend fun cleanRemoteKeys()
}