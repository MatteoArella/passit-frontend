package com.github.passit.data.datasource.local.model

import androidx.paging.DataSource
import androidx.room.*
import com.github.passit.data.datasource.local.db.ApplicationDatabase

@Dao
abstract class InsertionDao constructor(
    private val applicationDatabase: ApplicationDatabase
) {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun create(insertionLocalData: InsertionLocalData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(insertionLocalData: List<InsertionLocalData>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWithTutors(insertionAndTutorLocalData: List<InsertionAndTutorLocalData>) {
        applicationDatabase.userDao().insertAll(insertionAndTutorLocalData.map { it.tutor })
        insertAll(insertionAndTutorLocalData.map { it.insertion })
    }

    @Transaction
    @Query("SELECT * FROM insertions "
            + "WHERE subject LIKE :subject AND city LIKE :city AND state LIKE :state AND country LIKE :country AND status = 'OPEN' "
            + "ORDER BY updated_at DESC")
    abstract fun getInsertionsAndTutor(subject: String, city: String, state: String, country: String): DataSource.Factory<Int, InsertionAndTutorLocalData>

    @Transaction
    @Query("SELECT * FROM insertions "
            + "WHERE tutor_id = :tutorId "
            + "ORDER BY updated_at DESC")
    abstract fun getInsertionsByTutor(tutorId: String): DataSource.Factory<Int, InsertionAndTutorLocalData>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateInsertion(insertionLocalData: InsertionLocalData)

    @Query("DELETE FROM insertions")
    abstract suspend fun clearInsertions()
}