package com.github.passit.data.datasource.local.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface InsertionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(insertionLocalData: InsertionLocalData)
}