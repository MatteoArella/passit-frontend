package it.uniroma1.macc.project.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import it.uniroma1.macc.project.domain.repository.StorageRepository
import it.uniroma1.macc.project.repository.AWSStorageRepository
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class StorageModule {

    @Singleton
    @Binds
    abstract fun bindStorageRepository(
        awsStorageRepository: AWSStorageRepository
    ): StorageRepository
}

