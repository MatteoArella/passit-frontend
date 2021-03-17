package com.github.passit.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import com.github.passit.domain.repository.InsertionRepository
import com.github.passit.data.repository.InsertionRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class InsertionModule {

    @Singleton
    @Binds
    abstract fun bindInsertionRepository(
        insertionRepositoryImpl: InsertionRepositoryImpl
    ): InsertionRepository
}

