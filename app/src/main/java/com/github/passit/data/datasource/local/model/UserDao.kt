package com.github.passit.data.datasource.local.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(userLocalData: UserLocalData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usersLocalData: List<UserLocalData>)

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}