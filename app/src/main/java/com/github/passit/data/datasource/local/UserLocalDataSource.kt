package com.github.passit.data.datasource.local

import com.github.passit.data.datasource.local.db.ApplicationDatabase
import com.github.passit.data.datasource.local.model.UserLocalData
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val applicationDatabase: ApplicationDatabase
) {
    suspend fun createUser(user: UserLocalData) {
        applicationDatabase.userDao().create(user)
    }
}