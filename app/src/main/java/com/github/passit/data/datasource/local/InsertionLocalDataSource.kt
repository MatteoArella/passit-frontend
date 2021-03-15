package com.github.passit.data.datasource.local

import com.github.passit.data.datasource.local.db.ApplicationDatabase
import com.github.passit.data.datasource.local.model.InsertionLocalData
import javax.inject.Inject

class InsertionLocalDataSource @Inject constructor(
    private val applicationDatabase: ApplicationDatabase
) {
    fun createInsertion(insertion: InsertionLocalData) {
        applicationDatabase.insertionDao().create(insertion)
    }
}