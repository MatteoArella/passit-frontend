package com.github.passit.di

import com.github.passit.data.repository.StorageRepositoryImpl
import com.github.passit.domain.repository.StorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class StorageModule {

    @Singleton
    @Binds
    abstract fun bindStorageRepository(
            storageRepositoryImpl: StorageRepositoryImpl
    ): StorageRepository
}

